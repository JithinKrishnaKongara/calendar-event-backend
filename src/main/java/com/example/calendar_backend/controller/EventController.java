package com.example.calendar_backend.controller;

import com.example.calendar_backend.exception.EventOverlapException;
import com.example.calendar_backend.model.Event;
import com.example.calendar_backend.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins="http://localhost:3000")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Event e) {
        try {
            Event created = service.create(e);
            return ResponseEntity.ok(created);
        } catch (EventOverlapException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public List<Event> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) boolean remaining
    ) {
        if (start != null && end != null) {
            return service.getEventsInRange(start, end);
        }
        LocalDate d = (date != null ? date : LocalDate.now());
        return remaining ? service.getRemaining(d) : service.getEvents(d);
    }

    @GetMapping("/availability")
    public Map<String,String> availability(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam int durationMinutes
    ) {
        LocalDate d = (date != null ? date : LocalDate.now());
        return service.nextAvailable(d, durationMinutes)
                .map(slot -> Map.of("start", slot.getKey().toString(), "end", slot.getValue().toString()))
                .orElseThrow(() -> new EventOverlapException("Slot not available for the given time"));
    }

    @ExceptionHandler(EventOverlapException.class)
    public ResponseEntity<Map<String,String>> handleOverlap(EventOverlapException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

}
