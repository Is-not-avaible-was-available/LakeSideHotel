package com.learning.LakeSideHotel.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.learning.LakeSideHotel.Exception.NotFoundException;
import com.learning.LakeSideHotel.Exception.PhotoRetrievalException;

import java.util.ArrayList;
import java.util.List;
import com.learning.LakeSideHotel.Model.BookedRoom;
import com.learning.LakeSideHotel.Model.Room;
import com.learning.LakeSideHotel.Response.BookingResponse;
import com.learning.LakeSideHotel.Response.RoomResponse;
import com.learning.LakeSideHotel.Service.BookingService;
import com.learning.LakeSideHotel.Service.IRoomService;

@RestController
@RequestMapping("/rooms")
@CrossOrigin("http://localhost:5173")
public class RoomController {

    private final IRoomService roomService;
    private final BookingService bookingService;

    public RoomController(IRoomService roomService, BookingService bookingService){
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo
    ,@RequestParam("roomType") String roomType
    ,@RequestParam("roomPrice") BigDecimal roomPrice) throws SerialException, IOException, SQLException
    {
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = new RoomResponse(savedRoom.getId(),savedRoom.getRoomType(),
        savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);   
    }

    @GetMapping("/room-types")
    public ResponseEntity<List<String>> getRoomTypes(){
        List<String> roomTypes = roomService.getAllRoomTypes();
        return ResponseEntity.ok().body(roomTypes);
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws NotFoundException, SQLException, PhotoRetrievalException{

        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();

        for(Room room : rooms){
            byte[] photoBytes =roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes  !=null && photoBytes.length > 0){
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room); 
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok().body(roomResponses);
    }

    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable("roomId") Long roomId) throws NotFoundException{

        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/room/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable("id") Long roomId,
     @RequestParam(required = false)String roomType,
     @RequestParam(required = false)BigDecimal roomPrice, 
     @RequestParam(required = false)MultipartFile photo) throws IOException, NotFoundException, SQLException, PhotoRetrievalException{

        byte[] photoBytes = photo !=null && !photo.isEmpty()?
        photo.getBytes():roomService.getRoomPhotoByRoomId(roomId);

        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;

        Room theRoom = roomService.updateRoom(roomId, roomPrice, roomType, photoBytes);
        theRoom.setPhoto(photoBlob);

        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable("id")Long id) throws NotFoundException, SQLException, PhotoRetrievalException{

        Room room = roomService.getRoomById(id);

        RoomResponse roomResponse = getRoomResponse(room);

        return ResponseEntity.ok(roomResponse);
    }

    public RoomResponse getRoomResponse(Room room) throws NotFoundException, SQLException, PhotoRetrievalException{
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings
        .stream()
        .map(booking -> new BookingResponse(booking.getBookingId(), booking.getCheckInDate(),
        booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList(); 
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob!=null){
            try{
                photoBytes = photoBlob.getBytes(1, (int)photoBlob.length());
            }catch(SQLException e){
                throw new PhotoRetrievalException("Error fetching photo!");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(), room.isBooked(), 
        bookingInfo, photoBytes);
    }

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
     }
}
