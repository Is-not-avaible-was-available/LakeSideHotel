package com.learning.LakeSideHotel.Service;

import java.io.IOException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import com.learning.LakeSideHotel.Exception.NotFoundException;
import com.learning.LakeSideHotel.Exception.PhotoRetrievalException;
import com.learning.LakeSideHotel.Model.Room;
import com.learning.LakeSideHotel.Repository.RoomRepository;
@Service
public class RoomService implements IRoomService{
    private RoomRepository roomRepository;

    public RoomService (RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }
    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SerialException, SQLException {

        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
           Blob photoBlob = new SerialBlob(photoBytes);
           room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }


    @Override
    public List<String> getAllRoomTypes() {
       List<String> roomTypes = roomRepository.findDistinctRoomTypes();
       return roomTypes;
    }
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }


    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws NotFoundException, SQLException {
       Optional<Room> optionalRoom = roomRepository.findById(roomId);

       if(optionalRoom.isEmpty()){
        throw new NotFoundException("Room not found!");
       }

       Room room = optionalRoom.get();

       Blob photoBlob = room.getPhoto();
       if(photoBlob!=null){
        return photoBlob.getBytes(1, (int) photoBlob.length());
       }
       return null;
    }
    @Override
    public void deleteRoom(Long roomId) throws NotFoundException {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if(optionalRoom.isEmpty()){
         throw new NotFoundException("Room not found!");
        }
 
        Room room = optionalRoom.get();

        if(room !=null){
            roomRepository.deleteById(roomId);
        }
    }
    @Override
    public Room updateRoom(Long roomId, BigDecimal roomPrice, String roomType, byte[] photoBytes) throws NotFoundException, PhotoRetrievalException {
       Optional<Room> roomOptional = roomRepository.findById(roomId);
       if(roomOptional.isEmpty()){
        throw new NotFoundException("room not found!");
       }

       Room room = roomOptional.get();

       if(roomType != null) room.setRoomType(roomType);

       if(roomPrice != null) room.setRoomPrice(roomPrice);

       if(photoBytes !=null && photoBytes.length > 0){
        try{
            room.setPhoto(new SerialBlob(photoBytes));
        }catch(SQLException e){
            throw new PhotoRetrievalException("Exception occured updating room");
        }
       } 
        return roomRepository.save(room);
    }
    @Override
    public Room getRoomById(Long id) throws NotFoundException {
        Optional<Room> roomOptional = roomRepository.findById(id);
        if(roomOptional.isEmpty()){
         throw new NotFoundException("room not found!");
        }
 
        Room room = roomOptional.get();

        return room;
    }
    
}
