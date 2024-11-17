
package com.unisys.udb.user.service;

import java.util.List;
import java.util.UUID;

import com.unisys.udb.user.dto.request.PayeeRequest;
import com.unisys.udb.user.dto.response.DigitalCustomerPayeeResponse;
import com.unisys.udb.user.dto.response.PayeeResponse;
import com.unisys.udb.user.entity.FundTransferDropDown;


public interface DigitalPayeeService {
     void createPayee(PayeeRequest payeeRequest);
    List<DigitalCustomerPayeeResponse> deletePayeeServe(List<Integer> digitalCustomerPayeeId);
    List<DigitalCustomerPayeeResponse> getAllPayees(UUID digitalCustomerProfileId, String sortBy);

    boolean updatePayeeDetails(Integer digitalCustomerPayeeId, String payeeNickName, String payeeReference,
              String userName);
    Object getPayeeData(Integer digitalCustomerPayeeId);
    PayeeResponse getPayee(Integer digitalCustomerPayeeId);
    List<FundTransferDropDown> getFundTransferDropdownValues(String languageCode);
}

