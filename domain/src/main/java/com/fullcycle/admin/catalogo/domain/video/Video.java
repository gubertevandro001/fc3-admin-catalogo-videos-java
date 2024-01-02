package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.events.DomainEvent;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.genre.GenreValidator;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.time.Year;
import java.util.*;

public class Video extends AggregateRoot<VideoID> {

    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private boolean opened;
    private boolean published;

    private Rating rating;
    private Instant createdAt;
    private Instant updatedAt;
    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;
    private AudioVideoMedia trailer;
    private AudioVideoMedia video;
    private Set<CategoryID> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;


    protected Video(
            final VideoID anId,
            final String aTitle,
            final String aDescription,
            final Year aLaunchedYear,
            final double aDuration,
            final boolean wasOpened,
            final boolean wasPublished,
            final Rating aRating,
            final Instant aCreationDate,
            final Instant aUpdatedAt,
            final ImageMedia aBanner,
            final ImageMedia aThumb,
            final ImageMedia aThumbHalf,
            final AudioVideoMedia aTrailer,
            final AudioVideoMedia aVideo,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> castMembers,
            final List<DomainEvent> domainEvents) {
        super(anId, domainEvents);
        this.title = aTitle;
        this.description = aDescription;
        this.launchedAt = aLaunchedYear;
        this.duration = aDuration;
        this.opened = wasOpened;
        this.published = wasPublished;
        this.rating = aRating;
        this.createdAt = aCreationDate;
        this.updatedAt = aUpdatedAt;
        this.banner = aBanner;
        this.thumbnail = aThumb;
        this.thumbnailHalf = aThumbHalf;
        this.trailer = aTrailer;
        this.video = aVideo;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = castMembers;

    }

    public Video update(final String aTitle,
                        final String aDescription,
                        final Year aLaunchedYear,
                        final double aDuration,
                        final boolean wasOpened,
                        final boolean wasPublished,
                        final Rating aRating,
                        final Set<CategoryID> categories,
                        final Set<GenreID> genres,
                        final Set<CastMemberID> castMembers) {
        this.title = aTitle;
        this.description = aDescription;
        this.launchedAt = aLaunchedYear;
        this.duration = aDuration;
        this.opened = wasOpened;
        this.published = wasPublished;
        this.rating = aRating;
        this.setCategories(categories);
        this.setGenres(genres);
        this.setCastMembers(castMembers);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public static Video newVideo(
                                 final String aTitle,
                                 final String aDescription,
                                 final Year aLaunchedYear,
                                 final double aDuration,
                                 final boolean wasOpened,
                                 final boolean wasPublished,
                                 final Rating aRating,
                                 final Set<CategoryID> categories,
                                 final Set<GenreID> genres,
                                 final Set<CastMemberID> castMembers) {
        final var anId = VideoID.unique();
        final var now = InstantUtils.now();
        return new Video(anId, aTitle, aDescription , aLaunchedYear, aDuration, wasOpened,
                wasPublished, aRating, now, now, null, null, null, null,
                null, categories, genres, castMembers, null);

    }

    public static Video with(Video aVideo) {
        return new Video(aVideo.getId(), aVideo.getTitle(), aVideo.getDescription() , aVideo.getLaunchedAt(), aVideo.getDuration(),
                aVideo.isOpened(),
                aVideo.isPublished(), aVideo.getRating(), aVideo.getCreatedAt(), aVideo.getUpdatedAt(), aVideo.getBanner().orElse(null), aVideo.getThumbnail().orElse(null), aVideo.getThumbnailHalf().orElse(null), aVideo.getTrailer().orElse(null),
                aVideo.getVideo().orElse(null), new HashSet<>(aVideo.getCategories()), new HashSet<>(aVideo.getGenres()), new HashSet<>(aVideo.getCastMembers()), aVideo.getDomainEvents());
    }

    public static Video with(
            final VideoID anId,
            final String aTitle,
            final String aDescription,
            final Year aLaunchedYear,
            final double aDuration,
            final boolean wasOpened,
            final boolean wasPublished,
            final Rating aRating,
            final Instant aCreationDate,
            final Instant aUpdatedAt,
            final ImageMedia aBanner,
            final ImageMedia aThumb,
            final ImageMedia aThumbHalf,
            final AudioVideoMedia aTrailer,
            final AudioVideoMedia aVideo,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> castMembers
    ) {
        return new Video(anId, aTitle, aDescription , aLaunchedYear, aDuration,
                wasOpened,
                wasPublished, aRating, aCreationDate, aUpdatedAt, aBanner, aThumb, aThumbHalf, aTrailer,
                aVideo, categories, genres, castMembers, null);
    }




    @Override
    public void validate(final ValidationHandler handler){
        new VideoValidator(this, handler).validate();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isPublished() {
        return published;
    }

    public Rating getRating() {
        return rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Optional<AudioVideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public Set<CategoryID> getCategories() {
        return categories != null ? Collections.unmodifiableSet(categories) : Collections.emptySet();
    }

    public Set<GenreID> getGenres() {
        return genres != null ? Collections.unmodifiableSet(genres) : Collections.emptySet();
    }

    public Set<CastMemberID> getCastMembers() {
        return castMembers != null ? Collections.unmodifiableSet(castMembers) : Collections.emptySet();
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Video updateBannerMedia(ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video setBanner(ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateThumbnailMedia(ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video setThumbnail(ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateThumbnailHalfMedia(ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video setThumbnailHalf(ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateTrailerMedia(AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = InstantUtils.now();

        if (trailer != null && trailer.isPendingEncode()) {
            this.registerEvent(new VideoMediaCreated(getId().getValue(), trailer.rawLocation()));
        }

        return this;
    }

    public Video setTrailer(AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Video setVideo(AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Video updateVideoMedia(AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = InstantUtils.now();

        if (video != null && video.isPendingEncode()) {
            this.registerEvent(new VideoMediaCreated(getId().getValue(), video.rawLocation()));
        }

        return this;
    }

    private Video setCategories(Set<CategoryID> categories) {
        this.categories = categories != null ? new HashSet<>(categories) : Collections.emptySet();
        return this;
    }

    private Video setGenres(Set<GenreID> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : Collections.emptySet();
        return this;
    }

    private Video setCastMembers(Set<CastMemberID> castMembers) {
        this.castMembers = castMembers != null ? new HashSet<>(castMembers) : Collections.emptySet();
        return this;
    }

    public Video processing(final VideoMediaType type) {
        if (VideoMediaType.VIDEO == type) {
            getVideo().ifPresent(media -> updateTrailerMedia(media.processing()));
        } else if(VideoMediaType.TRAILER == type) {
            getTrailer().ifPresent(media -> updateTrailerMedia(media.processing()));
        }

        return this;
    }

    public Video completed(final VideoMediaType type, String encodedPath) {
        if (VideoMediaType.VIDEO == type) {
            getVideo().ifPresent(media -> setVideo(media.completed(encodedPath)));
        } else if(VideoMediaType.TRAILER == type) {
            getTrailer().ifPresent(media -> setTrailer(media.completed(encodedPath)));
        }

        return this;
    }
}
