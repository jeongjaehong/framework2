/* 
 * @(#)ValidationUtil.java
 */
package framework.util;

/**
 * ��ȿ�� üũ ���̺귯��
 */
public class ValidationUtil {

	/**
	 * �ֹε�Ϲ�ȣ/�ܱ��ε�Ϲ�ȣ ��ȿ�� üũ
	 * 
	 * @param residentRegistrationNo �ֹε�Ϲ�ȣ/�ܱ��ε�Ϲ�ȣ
	 * @return ��ȿ�� �ֹε�Ϲ�ȣ/�ܱ��ε�Ϲ�ȣ ���� ����
	 */
	public static boolean isResidentRegistrationNo(String residentRegistrationNo) {
		String juminNo = residentRegistrationNo.replaceAll("[^0-9]", "");
		if (juminNo.length() != 13) {
			return false;
		}
		int yy = to_int(juminNo.substring(0, 2));
		int mm = to_int(juminNo.substring(2, 4));
		int dd = to_int(juminNo.substring(4, 6));
		if (yy < 1 || yy > 99 || mm > 12 || mm < 1 || dd < 1 || dd > 31) {
			return false;
		}
		int sum = 0;
		int juminNo_6 = to_int(juminNo.charAt(6));
		if (juminNo_6 == 1 || juminNo_6 == 2 || juminNo_6 == 3 || juminNo_6 == 4) {
			//������
			for (int i = 0; i < 12; i++) {
				sum += to_int(juminNo.charAt(i)) * ((i % 8) + 2);
			}
			if (to_int(juminNo.charAt(12)) != (11 - (sum % 11)) % 10) {
				return false;
			}
			return true;
		} else if (juminNo_6 == 5 || juminNo_6 == 6 || juminNo_6 == 7 || juminNo_6 == 8) {
			//�ܱ���
			if (to_int(juminNo.substring(7, 9)) % 2 != 0) {
				return false;
			}
			for (int i = 0; i < 12; i++) {
				sum += to_int(juminNo.charAt(i)) * ((i % 8) + 2);
			}
			if (to_int(juminNo.charAt(12)) != ((11 - (sum % 11)) % 10 + 2) % 10) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �ֹε�Ϲ�ȣ/�ܱ��ε�Ϲ�ȣ ��ȿ�� üũ
	 * 
	 * @param juminNo �ֹε�Ϲ�ȣ/�ܱ��ε�Ϲ�ȣ
	 * @return ��ȿ�� �ֹε�Ϲ�ȣ/�ܱ��ε�Ϲ�ȣ ���� ����
	 */
	public static boolean isJuminNo(String juminNo) {
		return isResidentRegistrationNo(juminNo);
	}

	/**
	 * ���ι�ȣ ��ȿ�� üũ 
	 * 
	 * @param corporationRegistrationNo ���ι�ȣ
	 * @return ��ȿ�� ���ι�ȣ ���� ����
	 */
	public static boolean isCorporationRegistrationNo(String corporationRegistrationNo) {
		String corpRegNo = corporationRegistrationNo.replaceAll("[^0-9]", "");
		if (corpRegNo.length() != 13) {
			return false;
		}
		int sum = 0;
		for (int i = 0; i < 12; i++) {
			sum += ((i % 2) + 1) * to_int(corpRegNo.charAt(i));
		}
		if (to_int(corpRegNo.charAt(12)) != (10 - (sum % 10)) % 10) {
			return false;
		}
		return true;

	}

	/**
	 * ����ڵ�Ϲ�ȣ ��ȿ�� üũ
	 * 
	 * @param businessRegistrationNo ����ڵ�Ϲ�ȣ
	 * @return ��ȿ�� ����ڵ�Ϲ�ȣ ���� ����
	 */
	public static boolean isBusinessRegistrationNo(String businessRegistrationNo) {
		String bizRegNo = businessRegistrationNo.replaceAll("[^0-9]", "");
		if (bizRegNo.length() != 10) {
			return false;
		}
		int share = (int) (Math.floor(to_int(bizRegNo.charAt(8)) * 5) / 10);
		int rest = (to_int(bizRegNo.charAt(8)) * 5) % 10;
		int sum = (to_int(bizRegNo.charAt(0))) + ((to_int(bizRegNo.charAt(1)) * 3) % 10) + ((to_int(bizRegNo.charAt(2)) * 7) % 10) + ((to_int(bizRegNo.charAt(3)) * 1) % 10) + ((to_int(bizRegNo.charAt(4)) * 3) % 10) + ((to_int(bizRegNo.charAt(5)) * 7) % 10) + ((to_int(bizRegNo.charAt(6)) * 1) % 10) + ((to_int(bizRegNo.charAt(7)) * 3) % 10) + share + rest + (to_int(bizRegNo.charAt(9)));
		if (sum % 10 != 0) {
			return false;
		}
		return true;
	}

	/**
	 * �ſ�ī���ȣ ��ȿ�� üũ
	 * 
	 * @param creditCardNo �ſ�ī���ȣ
	 * @return ��ȿ�� �ſ�ī���ȣ ���� ����
	 */
	public static boolean isCreditCardNo(String creditCardNo) {
		return PatternUtil.matchCreditCardNo(creditCardNo).find();
	}

	/**
	 * ���ǹ�ȣ ��ȿ�� üũ
	 * 
	 * @param passportNo ���ǹ�ȣ
	 * @return ��ȿ�� ���ǹ�ȣ ���� ����
	 */
	public static boolean isPassportNo(String passportNo) {
		return PatternUtil.matchPassportNo(passportNo).find();
	}

	/**
	 * ���������ȣ ��ȿ�� üũ
	 * 
	 * @param driversLicenseNo ���������ȣ
	 * @return ��ȿ�� ���������ȣ ���� ����
	 */
	public static boolean isDriversLicenseNo(String driversLicenseNo) {
		return PatternUtil.matchDriversLicenseNo(driversLicenseNo).find();
	}

	/**
	 * �޴�����ȣ ��ȿ�� üũ
	 * 
	 * @param cellphoneNo �޴�����ȣ
	 * @return ��ȿ�� �޴�����ȣ ���� ����
	 */
	public static boolean isCellphoneNo(String cellphoneNo) {
		return PatternUtil.matchCellphoneNo(cellphoneNo).find();
	}

	/**
	 * �Ϲ���ȭ��ȣ ��ȿ�� üũ
	 * 
	 * @param telephoneNo ��ȭ��ȣ
	 * @return ��ȿ�� ��ȭ��ȣ ���� ����
	 */
	public static boolean isTelephoneNo(String telephoneNo) {
		return PatternUtil.matchTelephoneNo(telephoneNo).find();
	}

	/**
	 * �ǰ������ȣ ��ȿ�� üũ
	 * 
	 * @param healthInsuranceNo �ǰ������ȣ
	 * @return ��ȿ�� �ǰ������ȣ ���� ����
	 */
	public static boolean isHealthInsuranceNo(String healthInsuranceNo) {
		return PatternUtil.matchHealthInsuranceNo(healthInsuranceNo).find();
	}

	/**
	 * ���¹�ȣ ��ȿ�� üũ
	 * 
	 * @param bankAccountNo ������¹�ȣ
	 * @return ��ȿ�� ������¹�ȣ ���� ����
	 */
	public static boolean isBankAccountNo(String bankAccountNo) {
		return PatternUtil.matchBankAccountNo(bankAccountNo).find();
	}

	/**
	 * �̸����ּ� ��ȿ�� üũ
	 * 
	 * @param emailAddress �̸����ּ�
	 * @return ��ȿ�� �̸����ּ� ���� ����
	 */
	public static boolean isEmailAddress(String emailAddress) {
		return PatternUtil.matchEmailAddress(emailAddress).find();
	}

	/**
	 * �������ּ� ��ȿ�� üũ
	 * 
	 * @param ipAddress �������ּ�
	 * @return ��ȿ�� �������ּ� ���� ����
	 */
	public static boolean isIPAddress(String ipAddress) {
		return PatternUtil.matchIPAddress(ipAddress).find();
	}

	////////////////////////////////////////////////////////////////////////////////////////Private �޼ҵ�

	/**
	 * char�� ǥ���� ���ڸ� Ÿ���� int�� ����
	 */
	private static int to_int(char c) {
		return Integer.parseInt(String.valueOf(c));
	}

	/**
	 * String���� ǥ���� ���ڸ� Ÿ���� int�� ����
	 */
	private static int to_int(String s) {
		return Integer.parseInt(s);
	}
}
