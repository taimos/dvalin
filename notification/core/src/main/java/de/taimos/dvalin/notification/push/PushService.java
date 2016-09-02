package de.taimos.dvalin.notification.push;

public interface PushService {
    
    /**
     * register a device with the notification service. If the token is already registered, the existing internal id is returned
     *
     * @param deviceToken the device token
     * @param userData    the custom data to save with the device
     * @return the internal id of the registration
     */
    String registerDevice(String deviceToken, String userData);
    
    /**
     * send the message to the given device
     *
     * @param deviceId the internal id of the device
     * @param message  the message to send
     */
    void sendNotification(String deviceId, PushMessageWrapper message);
    
}
