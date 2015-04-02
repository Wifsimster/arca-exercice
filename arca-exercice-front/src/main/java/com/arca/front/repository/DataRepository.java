package com.arca.front.repository;

import com.arca.front.bean.Data;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataRepository extends MongoRepository<Data, String> {

//    public List<Data> findAll(long offset, long count);

//    public List<Data> findByCountry(@Param("country") String country);

    //public long getCount();
}
