package lf;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
    private static final String DATABASE_URL = "mongodb://localhost:27017"; // Change to your MongoDB URL
    private static final String DATABASE_NAME = "lost_and_found"; // Your database name
    private static MongoClient client;
    
    static {
        MongoClientURI uri = new MongoClientURI(DATABASE_URL);
        client = new MongoClient(uri);
    }

    public static MongoDatabase getDatabase() {
        return client.getDatabase(DATABASE_NAME);
    }
}
