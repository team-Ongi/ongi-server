package com.solution.Ongi.domain.push;

import com.solution.Ongi.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column
    private String token;

    @Column
    private String platform;

    @Column
    private String deviceId;

    @Column
    private boolean active;

    private LocalDateTime lastSeenAt;

    public static DeviceToken createDeviceToken(User user, String token, String platform, String deviceId){
        DeviceToken dt = new DeviceToken();
        dt.user = user;
        dt.token = token;
        dt.platform = platform;
        dt.deviceId = deviceId;
        dt.active = true;
        dt.lastSeenAt = LocalDateTime.now();
        return dt;
    }

    // 토큰을 계정 유저로 귀속, 메타 갱신
    public void rebindTo(User user, String platform, String deviceId){
        this.user=user;
        this.platform=platform;
        this.deviceId=deviceId;
    }

    public void markActive(){ this.active=true; }
    public void markInactive(){ this.active=false; }
    public void touchLastSeen(){ this.lastSeenAt=LocalDateTime.now();}


}
