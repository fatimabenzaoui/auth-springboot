package com.fb.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode(callSuper = false) @Builder
@Table(name="profile_photos")
public class ProfilePhoto implements Serializable {

    @Serial
    private static final long serialVersionUID = -8908114736129212900L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_photo_id")
    private Long profilePhotoId;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] photo;

    private String fileType;
    private String fileName;
}
