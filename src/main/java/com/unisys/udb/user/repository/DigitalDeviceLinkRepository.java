package com.unisys.udb.user.repository;

import com.unisys.udb.user.entity.DigitalCustomerDevice;
import com.unisys.udb.user.entity.DigitalDeviceLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.unisys.udb.user.constants.QueryConstants.*;

@Repository
public interface DigitalDeviceLinkRepository extends JpaRepository<DigitalDeviceLink, Integer> {

    DigitalDeviceLink findByDigitalCustomerDevice(DigitalCustomerDevice digitalCustomerDevice);

    @Query(value = FETCH_FROM_DIGITAL_DEVICE_LINK, nativeQuery = true)
    List<DigitalDeviceLink> getListOfDevicesLinkByDevices(@Param("digitalCustomerDeviceIds")
                                                          List<Integer> digitalCustomerDeviceIds);

    @Modifying
    @Transactional
    @Query(value = UPDATE_DIGITAL_DEVICE_LINK, nativeQuery = true)
    int deRegisteredDevices(@Param("deviceIdsList") List<Integer> deviceCustomerIds);

    @Query(value = FETCH_DIGITAL_CUSTOMER_DEVICE_IDS, nativeQuery = true)
    List<Integer> getRegisteredCustomerDevicesIds(UUID digitalCustomerProfileId);

    @Query(value = GET_DIGITAL_CUSTOMER_DEVICE_LINK, nativeQuery = true)
    DigitalDeviceLink findByDigitalCustomerDeviceID(@Param("digitalCustomerDeviceId")
                                                    Integer digitalCustomerDeviceId);

    @Modifying
    @Transactional
    @Query(value = UPDATE_DIGITAL_DEVICE_LINK_FOR_REGISTER_DEVICE, nativeQuery = true)
    int registerDevices(@Param("digitalCustomerProfileID")
                        UUID digitalCustomerProfileID,
                        @Param("digitalDeviceUUID")
                        String digitalDeviceUUID);

}