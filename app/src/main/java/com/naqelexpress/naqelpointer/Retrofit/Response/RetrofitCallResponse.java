package com.naqelexpress.naqelpointer.Retrofit.Response;

import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidationGWT;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnlineValidationOffset;

import java.util.List;

public class RetrofitCallResponse {

    public boolean HasError;
    public String ErrorMessage;
    public boolean IsEndOfTable;


    public class OnlineValidationResponse extends RetrofitCallResponse {

        private List<OnLineValidation> OnLineValidationData;

        public List<OnLineValidation> getOnLineValidationData() {
            return OnLineValidationData;
        }
    }

    public class OnlineValidationGWTResponse extends RetrofitCallResponse {

        private List<OnLineValidationGWT> OnLineValidationData;

        public List<OnLineValidationGWT> getOnLineValidationData() {
            return OnLineValidationData;
        }
    }

    public class OnlineValidationOffsetResponse extends RetrofitCallResponse {

        private List<OnlineValidationOffset> OnLineValidationData;

        public List<OnlineValidationOffset> getOnLineValidationData() {
            return OnLineValidationData;
        }
    }

}
