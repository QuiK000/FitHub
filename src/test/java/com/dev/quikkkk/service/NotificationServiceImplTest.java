package com.dev.quikkkk.service;

import com.dev.quikkkk.modules.notification.entity.Notification;
import com.dev.quikkkk.modules.user.entity.User;
import com.dev.quikkkk.modules.notification.enums.NotificationType;
import com.dev.quikkkk.modules.notification.event.NotificationEvent;
import com.dev.quikkkk.modules.notification.mapper.NotificationMapper;
import com.dev.quikkkk.modules.notification.repository.INotificationRepository;
import com.dev.quikkkk.modules.user.repository.IUserRepository;
import com.dev.quikkkk.modules.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private INotificationRepository notificationRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void createNotificationFromEvent_ShouldSaveToDbAndIncrementRedis() {
        String recipientId = "user-123";
        NotificationEvent event = NotificationEvent.builder()
                .recipientId(recipientId)
                .title("Test Title")
                .message("Test Message")
                .type(NotificationType.GENERAL_ANNOUNCEMENT)
                .build();

        User mockUserProxy = new User();
        mockUserProxy.setId(recipientId);

        Notification expectedNotification = Notification.builder()
                .recipient(mockUserProxy)
                .title("Test Title")
                .message("Test Message")
                .read(false)
                .build();

        when(userRepository.getReferenceById(recipientId)).thenReturn(mockUserProxy);
        when(notificationMapper.toEvent(any(User.class), any(NotificationEvent.class))).thenReturn(expectedNotification);
        when(redisTemplate.hasKey("notification:unread:user:" + recipientId)).thenReturn(true);

        notificationService.createNotificationFromEvent(event);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertEquals("Test Title", savedNotification.getTitle());
        assertEquals("Test Message", savedNotification.getMessage());
        assertEquals(recipientId, savedNotification.getRecipient().getId());
        assertFalse(savedNotification.isRead());
        verify(valueOperations).increment("notification:unread:user:" + recipientId);
        verify(userRepository).getReferenceById(recipientId);
        verifyNoMoreInteractions(userRepository);
    }
}