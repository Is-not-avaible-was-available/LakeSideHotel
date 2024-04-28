package com.learning.LakeSideHotel.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.learning.LakeSideHotel.Exception.InvalidBookingRequestException;
import com.learning.LakeSideHotel.Exception.NotFoundException;
import com.learning.LakeSideHotel.Model.BookedRoom;
import com.learning.LakeSideHotel.Model.Room;
import com.learning.LakeSideHotel.Response.BookingResponse;
import com.learning.LakeSideHotel.Response.RoomResponse;
import com.learning.LakeSideHotel.Service.IBookingService;
import com.learning.LakeSideHotel.Service.IRoomService;

@RestController
@RequestMapping("/bookings")
@CrossOrigin("http://localhost:5173")
public class BookingController {

    private final IBookingService bookingService;
    private final IRoomService roomService;
    public BookingController(IBookingService bookingService, IRoomService roomService){
        this.bookingService = bookingService;
        this.roomService = roomService;
    }
    
    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() throws NotFoundException{
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoom bookedRoom : bookings){
            BookingResponse bookingResponse = getBookingResponse(bookedRoom);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
        try{

            BookedRoom booking = bookingService.findBookingByConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);

        }catch(NotFoundException exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,@RequestBody BookedRoom bookingRequest) throws NotFoundException{

        try{
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);

            return ResponseEntity.ok("Room booked successfully! Your booking confirmation code is "
                                                               +confirmationCode);
        }catch(InvalidBookingRequestException e){
            ResponseEntity.badRequest().body(e.getMessage());
        }
        return null;
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId){

        bookingService.cancelBooking(bookingId);
    }


    public BookingResponse getBookingResponse(BookedRoom bookedRoom) throws NotFoundException{
        Room room = roomService.getRoomById(bookedRoom.getRoom().getId());
        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice()); 
        return new BookingResponse(bookedRoom.getBookingId(), bookedRoom.getCheckInDate(), bookedRoom.getCheckOutDate(),
        bookedRoom.getGuestFullName(), bookedRoom.getGuestEmail(), bookedRoom.getNumOfAdults(), bookedRoom.getNumOfChildren(),
        bookedRoom.getTotalNumOfGuest(), bookedRoom.getBookingConfirmationCode(), roomResponse);
    }
}
