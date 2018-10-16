package pl.edu.agh.sportsApp.service;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import pl.edu.agh.sportsApp.model.EventChat;
import pl.edu.agh.sportsApp.model.PrivateChat;
import pl.edu.agh.sportsApp.model.User;
import pl.edu.agh.sportsApp.repository.EventChatRepository;
import pl.edu.agh.sportsApp.repository.PrivateChatRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatStorage {

    @NonNull
    PrivateChatRepository privateChatRepository;
    @NonNull
    EventChatRepository eventChatRepository;

    public EventChat createEventChat(){
        return eventChatRepository.save(new EventChat());
    }

    public PrivateChat createPrivateChat(){
        return privateChatRepository.save(new PrivateChat());   // TODO methods for private chats, participants
    }

    public Optional<EventChat> getEventChatById(Long chatId){
        return eventChatRepository.findById(chatId);
    }

    public Optional<PrivateChat> getPrivateChatById(Long chatId){
        return privateChatRepository.findById(chatId);
    }

    public void deletePrivateChat(Long chatId){
        privateChatRepository.deleteById(chatId);
    }

    public void deleteEventChat(Long chatId){
        eventChatRepository.deleteById(chatId);
    }

    public void addParticipant(PrivateChat privateChat, User user){
        privateChat.getParticipants().add(user);
        privateChatRepository.save(privateChat);
    }

    public void removeParticipant(PrivateChat privateChat, User user){
        privateChat.getParticipants().remove(user);
        privateChatRepository.save(privateChat);
    }


}
