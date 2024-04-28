package com.learning.LakeSideHotel.Response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;;

@Getter
@Setter
@NoArgsConstructor

public class RoomResponse {

    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photo;
    private List<BookingResponse> bookings;


    public RoomResponse(Long id, String roomType, BigDecimal roomPrice){
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    
    public RoomResponse(Long id, String roomType, BigDecimal roomPrice,
     boolean isBooked,
     List<BookingResponse> bookings, byte[] photoBytes){

        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.bookings = bookings;
        this.photo = photoBytes!=null ? org.apache.tomcat.util.codec.binary.Base64.encodeBase64String(photoBytes):null;
    }
}
