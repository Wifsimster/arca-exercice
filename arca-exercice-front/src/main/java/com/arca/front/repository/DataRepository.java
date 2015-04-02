package com.arca.front.repository;

import com.arca.front.bean.Data;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataRepository extends MongoRepository<Data, String> {
}
