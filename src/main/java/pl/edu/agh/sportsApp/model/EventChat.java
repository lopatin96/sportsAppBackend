package pl.edu.agh.sportsApp.model;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity(name = "eventChat")
public class EventChat extends Chat{

}
