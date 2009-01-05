package org.loandb.persistence;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.loandb.persistence.model.*;
import org.loandb.persistence.types.AddressType;
import org.loandb.persistence.types.ApplicantRole;
import org.loandb.persistence.types.LoanType;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * LoanDB project (http://code.google.com/p/loandb/)
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since Jan 4, 2009
 */
public class CrudTest extends AbstractTest {

  @Test
  @Rollback(false)
  public void testCRUD() {
    Address residentialAddress1 = new Address();
    residentialAddress1.setAddressLine1("4209 Van Buren Dr");
    residentialAddress1.setAddressLine2("Apt # 158");
    residentialAddress1.setAddressType(AddressType.RESIDENTIAL);
    residentialAddress1.setCounty("POLK");
    residentialAddress1.setCity("WEST DES MOINES");
    residentialAddress1.setPostalCode("50266");
    residentialAddress1.setStateCode("IA");

    Address residentialAddress2 = new Address();
    residentialAddress2.setAddressLine1("1265 11th ST");
    residentialAddress2.setAddressLine2("Apt # 208");
    residentialAddress2.setAddressType(AddressType.RESIDENTIAL);
    residentialAddress2.setCounty("POLK");
    residentialAddress2.setCity("WEST DES MOINES");
    residentialAddress2.setPostalCode("50265");
    residentialAddress2.setStateCode("IA");

    Address residentialAddress3 = new Address();
    residentialAddress3.setAddressLine1("1660 Country Manor Blvd");
    residentialAddress3.setAddressLine2("Apt # 226B");
    residentialAddress3.setAddressType(AddressType.RESIDENTIAL);
    residentialAddress3.setCounty("YELLOWSTONE");
    residentialAddress3.setCity("BILLINGS");
    residentialAddress3.setPostalCode("59102");
    residentialAddress3.setStateCode("MT");

    Address propertyAddress1 = new Address();
    propertyAddress1.setAddressLine1("1120 Jordan Creek PKWY");
    propertyAddress1.setAddressLine2("Unit # 123");
    propertyAddress1.setAddressType(AddressType.PROPERTY);
    propertyAddress1.setCounty("POLK");
    propertyAddress1.setCity("WEST DES MOINES");
    propertyAddress1.setPostalCode("50265");
    propertyAddress1.setStateCode("IA");

    Address propertyAddress2 = new Address();
    propertyAddress2.setAddressLine1("1120 Jordan Creek PKWY");
    propertyAddress2.setAddressLine2("Unit # 213");
    propertyAddress2.setAddressType(AddressType.PROPERTY);
    propertyAddress2.setCounty("POLK");
    propertyAddress2.setCity("WEST DES MOINES");
    propertyAddress2.setPostalCode("50265");
    propertyAddress2.setStateCode("IA");

    Address propertyAddress3 = new Address();
    propertyAddress3.setAddressLine1("1120 Jordan Creek PKWY");
    propertyAddress3.setAddressLine2("Unit # 312");
    propertyAddress3.setAddressType(AddressType.PROPERTY);
    propertyAddress3.setCounty("POLK");
    propertyAddress3.setCity("WEST DES MOINES");
    propertyAddress3.setPostalCode("50265");
    propertyAddress3.setStateCode("IA");

    Applicant applicant1 = new Applicant();
    applicant1.setApplicantRole(ApplicantRole.PRIMARY);
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    try {
      applicant1.setDateOfBirth(dateFormat.parse("07/11/1960"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    applicant1.setSsn("000-00-0001");
    applicant1.setFirstName("KEN");
    applicant1.setLastName("CUSTOMER");
    applicant1.setResidentialAddress(residentialAddress1);

    Applicant applicant2Primary = new Applicant();
    applicant2Primary.setApplicantRole(ApplicantRole.PRIMARY);
    try {
      applicant2Primary.setDateOfBirth(dateFormat.parse("08/11/1960"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    applicant2Primary.setSsn("000-00-0002");
    applicant2Primary.setFirstName("ALAN");
    applicant2Primary.setLastName("APPLICANT");
    applicant2Primary.setResidentialAddress(residentialAddress2);

    Applicant applicant2Secondary = new Applicant();
    applicant2Secondary.setApplicantRole(ApplicantRole.SECONDARY);
    try {
      applicant2Secondary.setDateOfBirth(dateFormat.parse("09/11/1965"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    applicant2Secondary.setSsn("000-00-0003");
    applicant2Secondary.setFirstName("EMILY");
    applicant2Secondary.setLastName("APPLICANT");
    applicant2Secondary.setResidentialAddress(residentialAddress2);

    Applicant applicant3 = new Applicant();
    applicant3.setApplicantRole(ApplicantRole.PRIMARY);
    try {
      applicant3.setDateOfBirth(dateFormat.parse("10/11/1970"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    applicant3.setSsn("000-00-0004");
    applicant3.setFirstName("WILL");
    applicant3.setLastName("SMITH");
    applicant3.setResidentialAddress(residentialAddress3);

    Application application1 = new Application();
    application1.addApplicant(applicant1);
    application1.setSubmitDate(new Date());
    application1.setLoanAmount(120000.00);
    application1.setLoanType(LoanType.FIXED);
    application1.setPropertyAddress(propertyAddress1);
    applicationService.createApp(application1);

    Application application2 = new Application();
    application2.addApplicant(applicant2Primary);
    application2.addApplicant(applicant2Secondary);
    application2.setSubmitDate(new Date());
    application2.setLoanAmount(110000.00);
    application2.setLoanType(LoanType.ADJUSTABLE);
    application2.setPropertyAddress(propertyAddress2);
    applicationService.createApp(application2);
    assertEquals(2, application2.getApplicants().size());

    Application application3 = new Application();
    application3.addApplicant(applicant3);
    application3.setSubmitDate(new Date());
    application3.setLoanAmount(90000.00);
    application3.setLoanType(LoanType.FIXED);
    application3.setPropertyAddress(propertyAddress3);
    applicationService.createApp(application3);

    //added three applications to loandb
    assertEquals(3, applicationService.getAll().size());

    //get credit
    CreditBureauSummary cbrSummary1 = creditService.getCredit(applicant1.getId());
    //save credit response
    applicationService.saveCreditResponse(applicant1, cbrSummary1);

    //get credit
    CreditBureauSummary cbrSummary2 = creditService.getCredit(applicant2Primary.getId());
    //save credit response
    applicationService.saveCreditResponse(applicant2Primary, cbrSummary2);

    //get credit
    CreditBureauSummary cbrSummary3 = creditService.getCredit(applicant2Secondary.getId());
    //save credit response
    applicationService.saveCreditResponse(applicant2Secondary, cbrSummary3);

    //get credit
    CreditBureauSummary cbrSummary4 = creditService.getCredit(applicant3.getId());
    //save credit response
    applicationService.saveCreditResponse(applicant3, cbrSummary4);

    //get decision
    Decision decision = decisionService.getRiskDecision(application1.getId());
    //save decision response
    applicationService.saveDecisionResponse(application1, decision);

    //get decision
    Decision decision2 = decisionService.getRiskDecision(application2.getId());
    //save decision response
    applicationService.saveDecisionResponse(application2, decision2);

    //get decision
    Decision decision3 = decisionService.getRiskDecision(application3.getId());
    //save decision response
    applicationService.saveDecisionResponse(application3, decision3);
  }

}
