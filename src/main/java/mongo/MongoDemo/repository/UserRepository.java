package mongo.MongoDemo.repository;

import mongo.MongoDemo.document.UserDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserDocument, Long> {
}
