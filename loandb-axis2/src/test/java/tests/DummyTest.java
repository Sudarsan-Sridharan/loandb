package tests;

import org.junit.Test;
import org.loandb.persistence.AbstractTest;
import org.loandb.persistence.model.Address;
import org.loandb.persistence.model.Applicant;
import org.loandb.persistence.model.Application;
import org.loandb.persistence.types.AddressType;
import org.loandb.persistence.types.ApplicantRole;
import org.loandb.persistence.types.LoanType;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StopWatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;

import static junit.framework.Assert.assertEquals;

/**
 * This code is licensed under Apache License Version 2.0.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Apr 12, 2009
 */
public class DummyTest extends AbstractTest {

    @Test
    @Rollback(false)
    public void saveApp() {
        for (int i = 0; i < 100; i++) {
            Application application = new Application();
            application.addApplicant(applicant());
            application.setSubmitDate(new Date());
            application.setLoanAmount(150000.00);
            application.setLoanType(getRandomLoanType());
            application.setPropertyAddress(propertyAddress());
            applicationService.createApp(application);
        }
    }

    @Test
    @Rollback(false)
    public void getApp() {
        StopWatch watch = new StopWatch("testApplicationLoad");
        for (int i = 0; i < 5; i++) {
            watch.start();
            assertEquals(100, applicationService.getAll().size());
            watch.stop();
            System.out.println("get time : " + watch.getTotalTimeMillis() + "ms.");
        }
    }

    private Applicant applicant() {
        Applicant applicant = new Applicant();
        applicant.setApplicantRole(ApplicantRole.PRIMARY);
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            applicant.setDateOfBirth(dateFormat.parse("07/11/1960"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicant.setSsn("000-00-0001");
        applicant.setFirstName("KEN");
        applicant.setLastName("CUSTOMER");
        applicant.setResidentialAddress(residentialAddress());
        return applicant;
    }

    private Address residentialAddress() {
        Address residentialAddress = new Address();
        residentialAddress.setAddressLine1("4209 Van Buren Dr");
        residentialAddress.setAddressLine2("Apt # 158");
        residentialAddress.setAddressType(AddressType.RESIDENTIAL);
        residentialAddress.setCounty("POLK");
        residentialAddress.setCity("WEST DES MOINES");
        residentialAddress.setPostalCode("50266");
        residentialAddress.setStateCode("IA");
        return residentialAddress;
    }

    private Address propertyAddress() {
        Address propertyAddress = new Address();
        propertyAddress.setAddressLine1("1120 Jordan Creek PKWY");
        propertyAddress.setAddressLine2("Unit # 123");
        propertyAddress.setAddressType(AddressType.PROPERTY);
        propertyAddress.setCounty("POLK");
        propertyAddress.setCity("WEST DES MOINES");
        propertyAddress.setPostalCode("50265");
        propertyAddress.setStateCode("IA");
        return propertyAddress;
    }

    private LoanType getRandomLoanType() {
        Random rnd = new Random();
        Object[] objs = EnumSet.allOf(LoanType.class).toArray();
        return (LoanType) objs[rnd.nextInt(objs.length)];
    }

}

