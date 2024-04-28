package com.learning.LakeSideHotel.Response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;

    
    private LocalDate checkInDate;


    private LocalDate checkOutDate;

    
    private String guestFullName;


    private String guestEmail;

    
    private int numOfAdults;

   
    private int numOfChildren;

  
    private int totalNumOfGuest;


    private String bookingConfirmationCode;


    private RoomResponse room;

    public BookingResponse(Long id, LocalDate checkInDate, LocalDate checkOutDate,
     String bookingConfirmationCode){
        this.bookingId = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
     }

     public BookingResponse(Long id, LocalDate checkInDate, LocalDate checkOutDate,
     String bookingConfirmationCode, int numOfAdults, int numOfChildren, int totalNumOfGuest,
      String guestEmail, String guestFullName, RoomResponse room){

        this.bookingId = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
        this.totalNumOfGuest = totalNumOfGuest;
        this.numOfAdults = numOfAdults;
        this.numOfChildren= numOfChildren;
        this.guestEmail = guestEmail;
        this.guestFullName = guestFullName;
        this.room = room;
     }
}

