package com.learning.LakeSideHotel.Service;

import java.util.List;

import com.learning.LakeSideHotel.Exception.InvalidBookingRequestException;
import com.learning.LakeSideHotel.Exception.NotFoundException;
import com.learning.LakeSideHotel.Model.BookedRoom;

public interface IBookingService {
    
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    public List<BookedRoom> getAllBookings();

    public BookedRoom findBookingByConfirmationCode(String confirmationCode);

    public String saveBooking(Long roomId, BookedRoom bookingRequest) throws InvalidBookingRequestException, NotFoundException;

    public void cancelBooking(Long bookingId);
}
