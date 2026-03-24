package com.EventManager.API.service;

import com.EventManager.API.Domain.address.Address;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.Domain.event.EventRequestDTO;
import com.EventManager.API.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {


    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(EventRequestDTO data, Event event){
        Address address = new Address();

        address.setCity(data.city());
        address.setUf(data.state());
        address.setEvent(event);


        return addressRepository.save(address);
    }

}
