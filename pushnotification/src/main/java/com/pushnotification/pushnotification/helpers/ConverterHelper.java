package com.pushnotification.pushnotification.helpers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterHelper {
    private final ModelMapper modelMapper;


    @Autowired
    public ConverterHelper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
//        configureMapper();
    }

    public <D, E> E mapToEntity(D dto, Class<E> entity){
        return modelMapper.map(dto, entity);
    }

    public  <E, D> D mapToDto(E entity, Class<D> dto){
        return modelMapper.map(entity, dto);
    }

}
