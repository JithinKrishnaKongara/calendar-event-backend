package com.example.calendar_backend.service;

import com.example.calendar_backend.exception.EventOverlapException;
import com.example.calendar_backend.model.Event;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final Map<LocalDate, List<Event>> store = new HashMap<>();

    public synchronized Event create(Event e) {
        if (e.getStartTime().equals(e.getEndTime())) {
            throw new EventOverlapException("Start time and end time cannot be the same");
        }
        if (e.getStartTime().isAfter(e.getEndTime())) {
            throw new EventOverlapException("Start time must be before end time");
        }

        List<Event> list = store.computeIfAbsent(e.getDate(), d -> new ArrayList<>());
        for (Event existing : list) {
            if (e.getStartTime().isBefore(existing.getEndTime()) &&
                    e.getEndTime().isAfter(existing.getStartTime())) {
                throw new EventOverlapException("Slot not available for the given time");
            }
        }
        list.add(e);
        list.sort(Comparator.comparing(Event::getStartTime));
        return e;
    }

    public List<Event> getEvents(LocalDate date) {
        return store.getOrDefault(date, Collections.emptyList());
    }

    public List<Event> getEventsInRange(LocalDate start, LocalDate end) {
        List<Event> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Event>> entry : store.entrySet()) {
            LocalDate d = entry.getKey();
            if (!d.isBefore(start) && !d.isAfter(end)) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    public List<Event> getRemaining(LocalDate date) {
        LocalTime now = LocalTime.now();
        return getEvents(date).stream()
                .filter(e -> e.getEndTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public Optional<Map.Entry<LocalTime, LocalTime>> nextAvailable(LocalDate date, int durationMinutes) {
        List<Event> events = getEvents(date);
        LocalTime pointer = LocalTime.of(0, 0);
        for (Event e : events) {
            if (pointer.plusMinutes(durationMinutes).isBefore(e.getStartTime()) ||
                    pointer.plusMinutes(durationMinutes).equals(e.getStartTime())) {
                return Optional.of(Map.entry(pointer, pointer.plusMinutes(durationMinutes)));
            }
            pointer = e.getEndTime();
        }
        // after last event until midnight
        if (pointer.plusMinutes(durationMinutes).isBefore(LocalTime.of(23, 59))) {
            return Optional.of(Map.entry(pointer, pointer.plusMinutes(durationMinutes)));
        }
        return Optional.empty();
    }
}
