package com.yz.oneapi.repo;

import com.yz.oneapi.config.DataConfigTest;
import com.yz.oneapi.config.OneApiConfig;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class FilePersistenceTest implements Serializable{

    @Test
    public void read(){
        OneApiConfig oneApiConfig = new OneApiConfig(DataConfigTest.dataSource());
        Persistence persistence = new FilePersistence(oneApiConfig);
        System.out.println(System.getProperty("user.dir"));
        Person read = persistence.read("test.json", Person.class);
        System.out.println(read);
    }
    @Test
    public void readlist(){
        OneApiConfig oneApiConfig = new OneApiConfig(DataConfigTest.dataSource());
        Persistence persistence = new FilePersistence(oneApiConfig);
        System.out.println(System.getProperty("user.dir"));
        List<Person> people = persistence.readList("test.json", Person.class);
        System.out.println(people);
    }
    @Test
    public void writeList(){
        OneApiConfig oneApiConfig = new OneApiConfig(DataConfigTest.dataSource());
        Persistence persistence = new FilePersistence(oneApiConfig);
        List<Person> list = new ArrayList<>();
        list.add(new Person("leo", 5));
        Person lucy = new Person("lucy", 3);
        list.add(lucy);

        persistence.write("test.json", list);
    }
    @Test
    public void writeObj(){
        OneApiConfig oneApiConfig = new OneApiConfig(DataConfigTest.dataSource());
        Persistence persistence = new FilePersistence(oneApiConfig);
        Person lucy = new Person("lucy", 3);

        persistence.write("test.json", lucy);
    }

}