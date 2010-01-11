package org.loandb.persistence;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.loandb.persistence.model.Address;
import org.loandb.persistence.model.Applicant;
import org.loandb.persistence.model.Application;
import org.loandb.persistence.types.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 14, 2009
 */
public class ApplicationRevisionTest extends AbstractSpringTest {
    public void testSave() {
        Application application = new Application();
        application.addApplicant(applicant());
        application.setSubmitDate(new Date());
        application.setLoanAmount(150000.00);
        application.setLoanType(LoanType.ADJUSTABLE);
        application.setPropertyAddress(propertyAddress());
        applicationService.createApp(application);
        assertNotNull(applicationService.getAll().get(0));
    }

    public void testUpdateLoanAmount() {
        Application application = applicationService.getAll().get(0);
        assertNotNull(application);
        application.setLoanAmount(200000.00);
        applicationService.updateApp(application);
    }

    public void testUpdateLoanType() {
        Application application = applicationService.getAll().get(0);
        assertNotNull(application);
        application.setLoanType(LoanType.FIXED);
        applicationService.updateApp(application);
    }

    public void testRevision() {
        Application application = applicationService.getAll().get(0);
        assertNotNull(application);
        AuditReader reader = AuditReaderFactory.get(entityManager);
        Application application1_rev1 = reader.find(Application.class, application.getId(), 1);
        assertEquals(150000.00, application1_rev1.getLoanAmount());
        assertEquals(LoanType.ADJUSTABLE, application1_rev1.getLoanType());

        Application application1_rev2 = reader.find(Application.class, application.getId(), 2);
        assertEquals(200000.00, application1_rev2.getLoanAmount());
        assertEquals(LoanType.ADJUSTABLE, application1_rev2.getLoanType());

        Application application1_rev3 = reader.find(Application.class, application.getId(), 3);
        assertEquals(200000.00, application1_rev3.getLoanAmount());
        assertEquals(LoanType.FIXED, application1_rev3.getLoanType());
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
        applicant.setEmailAddress("ken@customer.com");
        applicant.setGenderType(GenderType.MALE);
        applicant.setIdentificationNumber("1234567890");
        applicant.setIdentificationAuthority("DOT");
        applicant.setIdentificationType(IdentificationType.LICENSE);
        try {
            applicant.setIdentificationIssueDate(dateFormat.parse("07/11/2007"));
            applicant.setIdentificationExpirationDate(dateFormat.parse("07/11/2012"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        applicant.setPhoneNumber("123-456-7890");
        applicant.setPhoneType(PhoneType.HOME);        
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

}
