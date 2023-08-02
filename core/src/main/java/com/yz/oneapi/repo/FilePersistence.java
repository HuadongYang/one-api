package com.yz.oneapi.repo;

import com.yz.oneapi.config.OneApiConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FilePersistence extends BasePersistence {

    public FilePersistence(OneApiConfig configuration) {
        super(configuration);
    }

    /**
     * @param <T>
     * @param fileName
     * @param t        需实现Serializable接口
     */
    @Override
    public <T> void write(String fileName, T t) {
        byte[] bytes = serialize(t);
        String path = configuration.getPersistencePath();

        try (FileOutputStream outputStream = new FileOutputStream(path + File.separator + fileName);) {
            outputStream.write(bytes);
        } catch (IOException e) {
            throw new PersistenceException("FilePersistence write Exception", e);
        }
    }

    @Override
    public <T> T read(String fileName, Class<T> clazz) {
        String path = configuration.getPersistencePath();
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path + File.separator + fileName));
            return deserialize(bytes, clazz);
        } catch (IOException e) {
            throw new PersistenceException("FilePersistence read Exception", e);
        }
    }

    @Override
    public <T> List<T> readList(String fileName, Class<T> clazz) {
        return read(fileName, List.class);
    }

    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(os);
            output.writeObject(t);
            //bugfix 解决readObject时候出现的eof异常
            output.writeObject(null);
            output.flush();
            data = os.toByteArray();
            // 关闭
            output.close();
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        try {
            ObjectInputStream input = new ObjectInputStream(is);
            Object result = input.readObject();
            return (T) result;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
