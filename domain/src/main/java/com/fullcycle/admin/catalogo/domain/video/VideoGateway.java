package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface VideoGateway {

    Video create(Video aVideo);

    Video update(Video aVideo);

    void deleteById(VideoID id);

    Optional<Video> findById(VideoID id);

    Pagination<VideoPreview> findAll(VideoSearchQuery query);

}
