package me.dlabaja.boompvp.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoData {
    //codec podle kterého se db řídí
    static CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    //vytvoření připojení
    private static final MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(Secrets.url))
            .codecRegistry(codecRegistry)
            .build();

    //připojení k db a kolekcím
    public static MongoClient mongoClient = MongoClients.create(clientSettings);
    public static MongoDatabase database = mongoClient.getDatabase("dblCraft");
    public static MongoCollection<MongoBoomPVP> coll = MongoData.database.getCollection("boompvp", MongoBoomPVP.class);
    public static MongoCollection<Document> collDoc = MongoData.database.getCollection("boompvp");
}
