package lf;

private void loadPosts() {
 // Clear the lists before reloading
 myPosts.clear();
 allPosts.clear();

 // Fetch all posts from MongoDB
 MongoCollection<Document> collection = MongoDBUtil.getDatabase().getCollection("lost_and_found");
 FindIterable<Document> iterable = collection.find();

 for (Document post : iterable) {
     String itemName = post.getString("itemName");
     String status = post.getString("status");
     String photoPath = post.getString("photoPath");
     String rollNo = post.getString("rollNo");

     // Combine all relevant details, including the photo path
     String postDetails = itemName + " - " + status + "|" + (photoPath != null ? photoPath : "No Image");

     if (rollNo.equals(loggedInUserRollNo)) {
         myPosts.add(postDetails); // Add to My Posts
     }

     allPosts.add(postDetails); // Add to All Posts
 }
}
