package com.library.service;

import com.library.dto.PenaltyRequest;
import com.library.dto.PenaltyResponse;
import com.library.entity.*;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ceza yönetimi işlemlerini yöneten servis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    /**
     * Yeni ceza ekler.
     */
    @Transactional
    public PenaltyResponse addPenalty(PenaltyRequest request) {
        Rental rental = rentalRepository.findById(request.getRentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Kiralama bulunamadı: ID=" + request.getRentalId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: ID=" + request.getUserId()));

        Penalty penalty = new Penalty();
        penalty.setRental(rental);
        penalty.setUser(user);
        penalty.setType(Penalty.PenaltyType.valueOf(request.getType().toUpperCase()));
        penalty.setAmount(BigDecimal.valueOf(request.getAmount()));
        penalty.setDescription(request.getDescription());

        Penalty saved = penaltyRepository.save(penalty);

        log.info("Ceza eklendi: {} → {} TL ({}) (ID={})",
                user.getEmail(), request.getAmount(), request.getType(), saved.getId());

        return toResponse(saved);
    }

    /**
     * Tüm cezaları getirir.
     */
    public List<PenaltyResponse> getAllPenalties() {
        return penaltyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kullanıcının tüm cezalarını getirir.
     */
    public List<PenaltyResponse> getPenaltiesByUser(Long userId) {
        return penaltyRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kiralamaya ait cezaları getirir.
     */
    public List<PenaltyResponse> getPenaltiesByRental(Long rentalId) {
        return penaltyRepository.findByRentalId(rentalId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Penalty entity'sini PenaltyResponse DTO'suna dönüştürür.
     */
    public PenaltyResponse toResponse(Penalty penalty) {
        PenaltyResponse response = new PenaltyResponse();
        response.setId(penalty.getId());
        response.setType(penalty.getType().name());
        response.setAmount(penalty.getAmount());
        response.setDescription(penalty.getDescription());
        response.setCreatedAt(penalty.getCreatedAt());

        if (penalty.getRental() != null) {
            response.setRentalId(penalty.getRental().getId());

            if (penalty.getRental().getBookCopy() != null &&
                    penalty.getRental().getBookCopy().getBook() != null) {
                response.setBookTitle(penalty.getRental().getBookCopy().getBook().getTitle());
            }
        }

        if (penalty.getUser() != null) {
            response.setUserName(penalty.getUser().getName() + " " + penalty.getUser().getSurname());
            response.setUserEmail(penalty.getUser().getEmail());
        }

        return response;
    }
}