package com.EventManager.API.service;

import com.EventManager.API.Domain.coupon.Coupon;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.Domain.event.EventDetailsDTO;
import com.EventManager.API.Domain.event.EventRequestDTO;
import com.EventManager.API.Domain.event.EventResponseDTO;
import com.EventManager.API.repositories.EventRepository;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private EventRepository repository;

    @Value("${aws.bucket.name}")
    private  String bucketName;



    public Event createEvent(EventRequestDTO data){
        String imgUrl = null;

        if(data.image() != null){
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventURL(data.eventUrl());
        newEvent.setDate(data.date());
        newEvent.setCity(data.city());
        newEvent.setState(data.state());
        newEvent.setImgURL(imgUrl);
        newEvent.setRemote(data.remote());
        System.out.println("URL da imagem: " + imgUrl);
        repository.save(newEvent);

        if (!newEvent.getRemote()){
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    public List<EventResponseDTO> getUpcomingEvents(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime now = LocalDateTime.now();
        Page<Event> eventsPage = this.repository.findUpcomingEvents(now, pageable);

        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddresses() != null ? event.getAddresses().getCity() : "",
                event.getAddresses() != null ? event.getAddresses().getUf() : "",
                event.getRemote(),
                event.getEventURL(),
                event.getImgURL())).stream().toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId){
       Event event = repository.findById(eventId)
               .orElseThrow(() -> new IllegalArgumentException("event not found"));
        LocalDateTime now = LocalDateTime.now();
       List<Coupon> coupons = couponService.consultCoupons(eventId,now);

       List<EventDetailsDTO.CouponDTO> couponsDTOs = coupons.stream()
               .map(coupon -> new EventDetailsDTO.CouponDTO(coupon.getCode(),
                       coupon.getDiscount(),
                       coupon.getValid()))
               .collect(Collectors.toList());

        return new EventDetailsDTO(event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddresses() != null ? event.getAddresses().getCity() : "",
                event.getAddresses() != null ? event.getAddresses().getUf() : "",
                event.getImgURL(),
                event.getEventURL(),
                couponsDTOs);
    }



    public List<EventResponseDTO> getFilteredEvents(int page, int size, String title, String city, String uf, LocalDateTime startDate, LocalDateTime endDate){
        LocalDateTime now = LocalDateTime.now();
        title = (title != null) ?title : "";
        city = (city != null) ?city : "";
        uf = (uf != null) ?uf : "";
        startDate = (startDate != null) ?startDate : now;
        endDate = (endDate != null) ?endDate : now.plusYears(10);
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = this.repository.findFilterEvents(title,city, uf,startDate,endDate,pageable);

        return eventsPage.map(event -> new EventResponseDTO(event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddresses() != null ? event.getAddresses().getCity() : "",
                event.getAddresses() != null ? event.getAddresses().getUf() : "",
                event.getRemote(),
                event.getEventURL(),
                event.getImgURL())).stream().toList();
    }

    private String uploadImg(MultipartFile multipartFile){
        String fileName = UUID.randomUUID() + "-"+ multipartFile.getOriginalFilename();

        try{
            File file = this.convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName,fileName, file);
            file.delete();
            return s3Client.getUrl(bucketName,fileName).toString();
        }catch (Exception e){
                e.printStackTrace();
            throw new RuntimeException("Erro ao fazer upload da image");
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

}
