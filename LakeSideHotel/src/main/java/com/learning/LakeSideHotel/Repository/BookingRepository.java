package com.learning.LakeSideHotel.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.LakeSideHotel.Model.BookedRoom;

public interface BookingRepository extends JpaRepository<BookedRoom, Long>{

    List<BookedRoom> findByRoomId(Long roomId);

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

}
