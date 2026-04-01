package com.material.manaement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.material.manaement.mapper.ConsultationMapper;
import com.material.manaement.model.entity.Consultation;
import com.material.manaement.service.ConsultationService;
import org.springframework.stereotype.Service;

@Service
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation>
        implements ConsultationService {
}
