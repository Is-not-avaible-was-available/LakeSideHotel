package com.learning.LakeSideHotel.Service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.learning.LakeSideHotel.Exception.InvalidBookingRequestException;
import com.learning.LakeSideHotel.Exception.NotFoundException;
import com.learning.LakeSideHotel.Model.BookedRoom;
import com.learning.LakeSideHotel.Model.Room;
import com.learning.LakeSideHotel.Repository.BookingRepository;


@Service
public class BookingService implements IBookingService{
    private final BookingRepository bookingRepository;

    private final IRoomService roomService;

    public BookingService(BookingRepository bookingRepository, IRoomService roomService){
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
    }


    @Override
    public List<BookedRoom> getAllBookings() {
       return bookingRepository.findAll();
    }

    

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }



    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) throws InvalidBookingRequestException, NotFoundException {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check in date must come before checkout date");
        }

        Room room = roomService.getRoomById(roomId);
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if(roomIsAvailable){
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        }else{
            throw new InvalidBookingRequestException("Invalid request: This room has been booked for the selected dates.");
        }

        return bookingRequest.getBookingConfirmationCode();
    }



    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
     }



     @Override
    public BookedRoom findBookingByConfirmationCode(String confirmationCode) {

        return bookingRepository.findByBookingConfirmationCode(confirmationCode);
    }



     private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings){

        return existingBookings.stream().noneMatch(existingBooking ->

        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
        || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
        || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
        && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

        && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

        && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
        && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
        && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))
        );
     }
}
