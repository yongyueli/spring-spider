package com.ti.dao;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoLianJia {
    MongoClient mongoClient;
    MongoDatabase lianjiaDatabase;
    public MongoLianJia(){
        mongoClient = new MongoClient();
        lianjiaDatabase = mongoClient.getDatabase("lianjia");
    }

    public void save(String collectionName,List<Document> documentList){
        MongoCollection<Document> collection = lianjiaDatabase.getCollection(collectionName);
        collection.insertMany(documentList);
    }


    public void save(String collectionName,Document document){
        MongoCollection<Document> collection = lianjiaDatabase.getCollection(collectionName);
        collection.insertOne(document);
    }
}
