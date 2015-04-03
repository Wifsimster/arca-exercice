package com.arca.front.repository;

import com.arca.front.bean.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface DataRepository extends MongoRepository<Data, String> {
}
