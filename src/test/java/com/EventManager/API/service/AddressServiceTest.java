package com.EventManager.API.service;

import com.EventManager.API.Domain.address.Address;
import com.EventManager.API.Domain.event.Event;
import com.EventManager.API.Domain.event.EventRequestDTO;
import com.EventManager.API.repositories.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
    class AddressServiceTest {

        @Mock
        private AddressRepository addressRepository;

        @InjectMocks
        private AddressService service;

    @Test
    void createAddress() {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(10);
        EventRequestDTO dto = new EventRequestDTO("Teste",
                "Teste case de sucesso",
                localDateTime ,
                "São Paulo",
                "SP",
                false,
                null, null);
        Event event = new Event();
        Mockito.when(addressRepository.save(Mockito.any(Address.class))).thenAnswer(
                Invocation -> Invocation.getArgument(0));

       Address address1 = service.createAddress(dto,event);

       assertNotNull(address1);
       assertEquals("São Paulo", address1.getCity());
        assertEquals("SP", address1.getUf());
       assertEquals(event , address1.getEvent());


    }
}