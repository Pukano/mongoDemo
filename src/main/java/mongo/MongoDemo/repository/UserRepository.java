package mongo.MongoDemo.repository;

import mongo.MongoDemo.document.UserDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserDocument, String> {
    List<UserDocument> findAll();
}
