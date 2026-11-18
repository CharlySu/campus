package es.academia.campushub.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import java.util.List;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoSchemaInitializer implements ApplicationRunner {
  static final String COLECCION = "seguimientos";

  private final MongoTemplate mongo;

  public MongoSchemaInitializer(MongoTemplate mongo) {
    this.mongo = mongo;
  }

  @Override
  public void run(ApplicationArguments args) {
    Document propiedades =
        new Document("actividad_id", new Document("bsonType", List.of("long", "int")))
            .append("tipo", new Document("bsonType", "string").append("minLength", 1))
            .append("creado_en", new Document("bsonType", "date"))
            .append("datos", new Document("bsonType", "object"));
    Document validador =
        new Document(
            "$jsonSchema",
            new Document("bsonType", "object")
                .append("required", List.of("actividad_id", "tipo", "creado_en", "datos"))
                .append("properties", propiedades));

    MongoDatabase db = mongo.getDb();
    if (mongo.collectionExists(COLECCION)) {
      db.runCommand(
          new Document("collMod", COLECCION)
              .append("validator", validador)
              .append("validationLevel", "strict")
              .append("validationAction", "error"));
    } else {
      db.createCollection(
          COLECCION,
          new CreateCollectionOptions()
              .validationOptions(
                  new ValidationOptions()
                      .validator(validador)
                      .validationLevel(ValidationLevel.STRICT)
                      .validationAction(ValidationAction.ERROR)));
    }
    db.getCollection(COLECCION)
        .createIndex(
            Indexes.ascending("actividad_id", "creado_en"),
            new IndexOptions().name("ix_actividad_creado"));
  }
}
