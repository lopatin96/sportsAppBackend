package pl.edu.agh.sportsApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.sportsApp.dto.EventDTO;
import pl.edu.agh.sportsApp.dto.EventRequestDTO;
import pl.edu.agh.sportsApp.dto.ResponseCode;
import pl.edu.agh.sportsApp.exceptionHandler.exceptions.ValidationException;
import pl.edu.agh.sportsApp.model.Event;
import pl.edu.agh.sportsApp.model.chat.EventChat;
import pl.edu.agh.sportsApp.model.User;
import pl.edu.agh.sportsApp.model.photo.EventPhoto;
import pl.edu.agh.sportsApp.repository.event.EventRepository;
import pl.edu.agh.sportsApp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.edu.agh.sportsApp.dto.ResponseCode.METHOD_ARGS_NOT_VALID;

@Service
public class EventService {
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private ChatStorage chatStorage;

    @Autowired
    public EventService(EventRepository eventRepository, UserRepository userRepository, ChatStorage chatStorage) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.chatStorage = chatStorage;
    }

    public void createEvent(EventRequestDTO eventRequestDTO, User owner) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (eventRequestDTO.getStartDate().isBefore(currentDate) || eventRequestDTO.getStartDate().isAfter(currentDate.plusMonths(1)))
            throw new ValidationException(METHOD_ARGS_NOT_VALID.name());
        EventChat eventChat = chatStorage.createEventChat();
        Event newEvent = eventRequestDTO.parseEvent();
        newEvent.setOwnerId(owner.getId());
        newEvent.setOwner(owner);
        Set<User> participants = new HashSet<>();
        participants.add(owner);
        newEvent.setParticipants(participants);
        newEvent.setEventChat(eventChat);
        eventRepository.save(newEvent);
    }

    public void addParticipant(Long eventId, Long participantId) {
        Event event = eventRepository.getOne(eventId);
        eventRepository.save(fillEntity(event, participantId));
    }

    public void removeParticipant(Long eventId, Long participantId) {
        Event event = eventRepository.getOne(eventId);
        eventRepository.save(fillEntity(event, participantId));
    }

    public Event getEvent(Long id) {
        return eventRepository.getOne(id);
    }

    @Transactional
    public void removeEvent(Event event) {
        eventRepository.deleteById(event.getId());
    }

    private Event fillEntity(Event event, Long participantId) {
        Set<User> users = event.getParticipants();
        users.add(userRepository.getOne(participantId));
        event.setParticipants(users);
        return event;
    }

    @Transactional
    public void deleteEvent(Long eventID, Long ownerId) {
        Event event = eventRepository.getOne(eventID);
        if (!event.getId().equals(ownerId))
            throw new AccessDeniedException(ResponseCode.ACCESS_DENIED.name());
        else
            eventRepository.deleteById(eventID);
    }

    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    public void addEventPhoto(Event event, EventPhoto eventPhoto) {
        event.addEventPhoto(eventPhoto);
        eventRepository.save(event);
    }

    public void removeEventPhoto(Event event, EventPhoto eventPhoto) {
        event.removeEventPhoto(eventPhoto);
        eventRepository.save(event);
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(Event::mapToDTO)
                .collect(Collectors.toList());
    }
}
