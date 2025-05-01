package gay.gwynnie.backpaws.photo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository
  extends MongoRepository<Photo, String> {}
