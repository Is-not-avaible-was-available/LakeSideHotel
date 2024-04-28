package com.learning.LakeSideHotel.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.springframework.web.multipart.MultipartFile;

import com.learning.LakeSideHotel.Exception.NotFoundException;
import com.learning.LakeSideHotel.Exception.PhotoRetrievalException;
import com.learning.LakeSideHotel.Model.Room;

public interface IRoomService {

    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException,SerialException, SQLException;

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws NotFoundException, SQLException;

    void deleteRoom(Long roomId) throws NotFoundException;

    Room updateRoom(Long roomId, BigDecimal roomPrice, String roomType, byte[] photoBytes) throws NotFoundException, PhotoRetrievalException;

    Room getRoomById(Long id) throws NotFoundException;

    
}
