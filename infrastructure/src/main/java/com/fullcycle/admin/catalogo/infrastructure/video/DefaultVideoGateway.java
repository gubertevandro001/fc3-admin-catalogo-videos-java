package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.configuration.anotations.VideoCreatedQueue;
import com.fullcycle.admin.catalogo.infrastructure.services.EventService;
import com.fullcycle.admin.catalogo.infrastructure.utils.SqlUtils;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final VideoRepository videoRepository;
    @VideoCreatedQueue
    private final EventService eventService;

    public DefaultVideoGateway(VideoRepository videoRepository, @VideoCreatedQueue EventService eventService) {
        this.videoRepository = videoRepository;
        this.eventService = eventService;
    }


    @Override
    @Transactional
    public Video create(Video aVideo) {
        final var result = this.videoRepository.save(VideoJpaEntity.from(aVideo)).toAggregate();
        aVideo.publishDomainEvents(this.eventService::send);
        return result;
    }

    @Override
    @Transactional
    public Video update(Video aVideo) {
        final var result = this.videoRepository.save(VideoJpaEntity.from(aVideo)).toAggregate();
        aVideo.publishDomainEvents(this.eventService::send);
        return result;
    }

    @Override
    public void deleteById(VideoID id) {
        final var aVideoId = id.getValue();
        if(this.videoRepository.existsById(aVideoId)) {
            this.videoRepository.deleteById(aVideoId);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Video> findById(VideoID id) {
        return this.videoRepository.findById(id.getValue()).map(VideoJpaEntity::toAggregate);
    }

    @Override
    public Pagination<VideoPreview> findAll(VideoSearchQuery query) {

        final var page = PageRequest.of(query.page(), query.perPage(), Sort.by(Sort.Direction.fromString(query.direction()), query.sort()));

        final var actualPage = this.videoRepository.findAll(
                SqlUtils.like(SqlUtils.upper(query.terms())),
                CollectionUtils.nullIfEmpty(mapTo(query.castMembers(), Identifier::getValue)),
                CollectionUtils.nullIfEmpty(mapTo(query.categories(), Identifier::getValue)),
                CollectionUtils.nullIfEmpty(mapTo(query.genres(), Identifier::getValue)),
                page
        );
        return new Pagination<>(actualPage.getNumber(), actualPage.getSize(), actualPage.getTotalElements(), actualPage.toList());
    }
}
