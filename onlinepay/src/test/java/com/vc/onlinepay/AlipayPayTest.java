/**
 * @类名称:PayTest.java
 * @时间:2018年6月14日下午3:59:44
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundCouponOrderPagePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayFundCouponOrderDisburseRequest;
import com.alipay.api.request.AlipayFundCouponOrderPagePayRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayMarketingCampaignCashCreateRequest;
import com.alipay.api.request.AlipayTradeOrderSettleRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayFundCouponOrderDisburseResponse;
import com.alipay.api.response.AlipayFundCouponOrderPagePayResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayMarketingCampaignCashCreateResponse;
import com.alipay.api.response.AlipayTradeOrderSettleResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.vc.onlinepay.utils.Constant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @描述:应用id
 * @时间:2018年6月14日 下午3:59:44 
 */
public class AlipayPayTest {
	private static String SIGN_TYPE = "RSA2";
	private static String FORMAT = "json";
	private static String CHARSET = "UTF-8";

	//手机wap
//    static String appId="2016111002703770";
//    static String privateKey="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDFcH0tQoH/zOjgbSrWqUV1mzJDzZ8DKdjfDsUXp47PbGn/4G9rYyTpvYICPXTvd2zb6VwEeP+anPkUJsrflNNncrw/0nMxl6bZtzXZIu929XU0DpmCIoltgklq0mAmS7Y8ziuKxorp1rwvv6+T6aEwfBLH7KovL1Gq5qwqzyLrZpobnkWzrFcob6seYFVNTuFaMXckLZ3/OxIxypNycksCJ8NHOiubvTFYauGpibWlZ6zY7kelR1BV01VJMC1hUrK0gFXRhAzHzYxHh5JQ2OFpZKndT/vT38KUjXjkgIbJl3b0NRby7aN8cYcxIM62Swlg0//sfxOduD0VTeVvA2glAgMBAAECggEAfcGziXN2OAc9QU1nXsj8W/dZIcTDhpFN+G2A7hskpEuKjJdYgI7qwRKaz3Bznd90b/IIUTT8oW9gWwyGZbh2msqCyyfxhWHxvXSrkL1mx196ooi2ECSNYjn9J1QON/Y7gilTpJXnfx/uR3+RhuXlbGtqzvqYP2ulWfSJLs82j7Zw8aO1xLC7GnYEIc6owit76j46x+hWQTXhmK5PJrXCaEjLlGC3qsn1il8Cz23HqXMyFB4mjacpNo5sGGcKpXSD7S3yvPyhR4HIzWEXtTh26ASPHnYN4yNs78+i/NLagQ2YP9rh/zqY6tuMJHRC/Cm+IAGg5R0A6jvdRcQHJl13wQKBgQDmNKNMqvH55XhU2kxq+T+We3OSnmfralFHbKhOVO7zQbhNZ87EyPtcvTk53p2aUMzSZnZyccSKwPIuRD9joSuas6p9sq8LlBiTVvB5fNzSC4sK7b4e48yR3c55EQrjXDKCRB7oEsd71EWve+6jMDJlwMRvxvLpn3SkiXB1gx2IFQKBgQDbj/b1ny+pK/lD4Ygb/6i9ZrEnnssQEPKU3aDzC+gMVjoYDXmLJP/0ELZ3Kcx2DRK9TvbQUIn49RxnN9kKrmdLmzEQP3ZU4fmlLXCuSapI80c2ZxinJvrMwSTMXooSHK4xAcfIYlZ+EiekRCLPlb4bA2guoAHW39jNjdmkO0bT0QKBgQCbeK9U5xqXQB/dhrUXhhAo7GUof20Fs/8CsuKUTI44oYj8VgByLJ5+ipkseSmNf8ZkzqhuMGGhqB+nmZYKDl5pk6+wRs4rjrBuScgM9IQaI4ubLOc663RMizWX1Ce7V5gRHDqXV598BOevVY7cjX9r8x13tDB4EaXwPZZC9vMp5QKBgQCM0bPx6wwVc3emutKiKoHeICwFHkpc3pHHOrJMz9kfeHpADPxkavGiZsmweeWB3XakZi3Ug6NKQmLaLK6OtDbZ/gokwp8xZ9HGx/MaRXLq4OVOlibwpgZ7JnFkhkoFvdFW6y5obfHNX/V+3Jh7DS5lgvTh2ovzdZRGmfD57W7K0QKBgEUeRWpfTIatVHfCATJhrX7Oq7goopLAxOPZw5ojWGmGoLwaVuckerXedwT8v+3tw4cwpuLpzSQqeEHKoetKs1g8TCMaevyLrr8Yq8WTtQfn0dzzIkuLvLBqaKqAGENrCaqJDWcILfDhZlEYLIZma31ZhKNDp9ZrqWpxybQrTGMx";
//    static String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxXB9LUKB/8zo4G0q1qlFdZsyQ82fAynY3w7FF6eOz2xp/+Bva2Mk6b2CAj1073ds2+lcBHj/mpz5FCbK35TTZ3K8P9JzMZem2bc12SLvdvV1NA6ZgiKJbYJJatJgJku2PM4risaK6da8L7+vk+mhMHwSx+yqLy9RquasKs8i62aaG55Fs6xXKG+rHmBVTU7hWjF3JC2d/zsSMcqTcnJLAifDRzorm70xWGrhqYm1pWes2O5HpUdQVdNVSTAtYVKytIBV0YQMx82MR4eSUNjhaWSp3U/709/ClI145ICGyZd29DUW8u2jfHGHMSDOtksJYNP/7H8Tnbg9FU3lbwNoJQIDAQAB";
//    static String aliPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjDlNzHaTYmtzrT2pmtd2lV6ksVq1O7TbxdXCThjwQFwAe5FLThzA0YEioG8jCMBEB2gHcux4pde9SI9MCzU6kkC+6O2jmLoZEq2keUwVeekVvZ7bR2V7C04eySASggdPwQE1y/IYDSYgEtn42c0mKzPXBiTI7R0Ar87yBfF6fpho6JEak4vqt9NxtjzRR845pLwWvniYN8lVQjxA90MlkBDat0oGDi9+N/eAbg9+MU1fy6+GFoWR7i4K/AIK6WAQe8o227LHQS76Xkvdtourw6xzG8t3fK8M3IYAxsbiYbwYRLQ+7wV15IRKH7xDXGeqalIledOvax171I/wPZkmLwIDAQAB";

	//口碑
//    static String appId="2018061760406471";
//    static String privateKey="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCMill66vJwpAQi1m2FUwbVCKQJygMW7y/shDaMcoeH3RVw89k4PWRZOUDU665zd3AhPGwSwjjA2kSXnV39jYy54flRQ7cyErtMfu9Z2DeuXR2d+iTFuqr14mrHqf4CpF8Pscw1Sje8vcyQ7uDMgq+Kx44OTavFCoFAc5779qePlR9FVyLtF3SxBZGaaitTO73C95v/SivrGky3cfQz+i5z0B6TjCPubw5K67AMlQp1NiU16XRjLCEXPsI1/cI5d9gH//bPSSC4eufCTOPqnafv8WBu4y7huXf2x6ETCC5cNeeU/J81LKnL8Ipdic58rZKGCnw4D7Xpfh1TDuQfl8IHAgMBAAECggEAMxZLhpoHPRjyA/PsZe2Tazcz4fcT/m89/XzmE1HCoImmNKP/jykJ5EEipwNxK7n8AajkJEGDvd7Z1j83HmcpA5z4MykpW8NhYGI4AXHOc8yXaoi2e0XqxWzjoebSVgWIJL1d3PU4o5xe00RyZQUQaKM3qU1kpkC2UUZ2QWMc790bCA80qXmY3ZpqqabXlP2CvvM26G7vw3BymR7Jc15pzCLOjB2d8Q7HWFZ0tCQD0oFh8womSIhXUEP/bjsu/S8NCHmI3acvOlRLOqve7kd16iGCKriH6Qry8ugireBY5AYbyeLjUEGZYw/Ay9d8+LcMLGUL7WTkHS7y3gOFMG3MMQKBgQDZl82NeIyoXPK8pOsfJUWpodNcX1wDds4W+yPoT2YrgO8GGyGXuupl4aW0bvhmZ+Dx97G5xiEaom36OJ7Pxmo8Smb6efNdMHO9Q2e8F4fZqZ+5suDGKNFO4EbZqtmq+Lnsjp8Wuj3vsz6pIEFmRDk3Z2hP2L9wA6kpOUc6HJCJGQKBgQClWNXngPYwb2s8VX6wwUwqwWKZDlil4Gj8sKx0Nmuop9hEDoCvAsGX9/32mgaYkeGqeab9HNOp9a1gbcm/lcXVhAlPIO030XMBixn3HJgDECIsMj0/NyQD7hj00a9Q+bvVtxBgEvH53cuCqH3m/OlDEMeWT8QkDB5u/c3cwS1oHwKBgCUFDLW7AsNKPQoXTOFZqAZ6P8mBd9gnCF0kkTFfANT59LCnQQndhS+cOmL+Vkil98FzEMu4h71OUUO3Cl3zNfPX/Lk0slDyg15571Yy8qv3jfM8Gb3dB+9RfPxfcvUBqDA0+6AUQ6Fab5i6oiXSCaIBd9/N0pQuK0OWOSYL4hNRAoGBAJqmUUM7UoiQ3brLuDg4QzZGNCM10OIs1+VolZQIhn9zpYpaCjAmos2xzN6Gx56R64DYUcCx4qM3vxbO+QDxY91FprNU96ragvexk/KV0vQidePihXiH6W6nlr39FT3mprHIjE9fyOVMdplZYJDYgpAQtKy5JJoWn6xuRMqx//c/AoGAN84SdwmDLADCbzYIAMA/Qm/zQ67c+Mppgkzb28KUxfZhmb43M3GonjIcU6eSrBMXYVei4KJCUUVHeiirEHhOU7WIUARCC+YHRuKaNoEbSXG/Lcq8KZvJUaOyQjYYB+7o77luon5S9P5982YxPubkF8iRzty/jzTMpCAObcWyrl0=";
//    static String aliPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoZMHWh/67lD8Urv2ahKMWX/CotxsAJrVL3g+nofxyrryV0Aldu7rmn9vi2gq08sWIK8jJtd7WwjpE+VtSHNhfym7/J6HGCbZOVO9SMsM/9l54p3dD0ihM9BR+nunrKSsMVQqv8vAHG5L0BAVoIX4/3qogrnhkqtTp3dNL4caYuff+1gGorPegAvUoUTJXZva8SigPmoFx41S/JaY9BWkwUAKn+iiEAFQTpUYMgPKVOha/hcDUvWjWJAmR/3nDEtwL8D4VcJQTLCKmim5y+zjhxgBFNAb4EtZOgsBobaHdqPI+WWgRTVxtwDTQFs0knvElzsg+8WIFYMW8VrPvmU0iQIDAQAB";

	//口碑 苏州娄岑进出口有限公司
//    static String appId="2016122704668689";
//    static String privateKey="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC5OdtHV6uQHmYkPyNo+do2ygxJ5EMOaCOlsAOlb/J+lo4/Mm9tpT4gDj7jZPK0Whjo/H2TmyDqxC7iKn8gLYg+VYvAFFYUdH/mw0P8jk27sfsP1iR+NLxuIOZ40EYG1V+NXV2jjdE/KJK5DOwYpNk0hKhJIwYWY1kegW98zE0YUNGEkA/VwFdoF9qxgLG+TSlndUYW/SiK6lMewhzUvOgS5Hj5JnDNdMUOGqVIiyhugaau/Ad51uZ0F576MBpjztVTLYRQzdVd9fANNk5ZXAycebOdE5nNuHMbEbgwNY4jPkg4qdRWyDf3+Tw3WSush9QZozSCX8qll84++LqwnoVHAgMBAAECggEAfs/6reR7BEebvLMxhhyRZrN8KFbMBiAMT9KrHGgBiOOA7gmUumfImd/G9J5XgFyy80hynU03AyRfyN45fQlPXwXokRAbQxPjwVTsxguTcq7O6a+9+l4Wp/uEbzCA0QriyYEjq+dbXDdmBKWxOsoaH1e3c6Mz9r379OO/NZ0WJ09373N9yE3zYsJFSJ7OD6m9XB4kLowDKvO/LRLHoweTHQ5WvrzVbE2Ok7/jk+s5Y4+0sZmRN1Sbc+iZ1/T3+K0NhbaKkpX8NgLZsU+UhWN5j2NYHwwK1Un8nw8qw+UwiJVYUjK+baYbMJSDvpr5Iuuy8L/HonktjHekcFh92bMniQKBgQD+RUqGgzXWeNvjQCF5Y0PRIppVZFLbmQ8l8SWIMhbc0a1BrgvL4acC9Pf+NQNZSQ3nngwn9+3NXD7oMM+qeNTnCQw1i21EIBxyOE6OkwNGRJ6/E0bUGO2gdpmObIe5kaZUg7u0nXoAjRXlzRyaozmUX8Dfp8QDRgW3lXQV+pXOmwKBgQC6fFotdZjdvHCt7xBGZOh8mMBxMux5fN8BDv8GYZlZMad6f30Y7iMtB2TxZa9i7mwMVX+c3LQA/vKLUadSnFq5NPq+G3yzy0tS2HPZPRV8A0Kqt+NSM8ToH5bsJ/KdBukkSqusksAcTRQYSvdjjAlj20HVZy/2PYTnVcvKM2gYxQKBgDvXkvKVbtZLG3UgYsBu3EccHbZ7JgUIwMftOSgUoczWjDApBh21vdi0TqlFUMRyrRMV30aI0y7BxwxecIFubqzxNC5l4hFmfUtkdRiZAOxIuhO+Na01MLfTMdCTOl4yKTM6zbJ0XYJDsiOjYlgCkkqZPotksH0UvcO3KNsDXr6BAoGAdB9E4/PLOZ/CpnwrrTnejt9rTdHA8LFyB6233dhebez+dcf3NNPssXheQfborHj8bP28VggUSl5214wD3ewgjh1QWZqyHx7jC3ZfRRHFVBCPrdCNy70SytwI1YrWhzMrb/7TQcWrvLO+ZJYh0Cn+GuEH8IOtmO6b2JVTLdiu5GUCgYEAw7Jnfo20jQVNLZwBMb1wbf56fy5w0JVCpeumKPplhyPtCnoPsnnDWsdjr2NM80/veUv2OpFgq66L+S/lEDz5h/vHl1+encN+poyK1gimW953lUY8qY9FFOVoCZMPCmSptzoyTVtPziP1XKOTEMdOuSRwu3Wq9Arp2J6FUBU7P/A=";
//    static String aliPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnhcXvtu1OIKJvq1bGbIIxRonaswfSbnR6TkdP+l3BZF/9+CKQFObG//j5LMWVn7kwyXtyFNZCdUCagN4IxcKwd/8Uzme7AytFM/5eCk9MMwU96cl2Lixh1WMhRN3e4/j3b23/zP63V+bbgIiPz3EP7nRCxH0ePyO/PT24Wp2mVPhZdxoMS9JLyV7wnoI/Tnt2CTiL9aTPQVnD9k/54sY67IFZCMUyPnoGOKpox0VcJXw39exLc8U/wWXTAro/yAGInaBlMXwTV6X8q/O71JMDOE9oRFsQrcAhlMLR193730BZqE0IurI+adHPEqiJVQswg760zobjg2ky3CwvP3+tQIDAQAB";

	//红包 荣耀1
//	static String APP_ID="2018122762707231";
//	static String APP_PRIVATE_KEY="MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDTd07GIB0zBDjPpMa7LhR0gbAYHB84ie/h2nAhVWZnXK13WE2XAkUymDS0wBSsY/Fm5GgtNqj5a27+XvOTshBhYK1QZSjjcmEpdKQLcMGusATNL+MkSIwitEA+hz6IA9b61EqyZ4bQEeFVbE5MV+i6+3iYJYDV2OwBwUN2GZfXFO+mm4MgwqF3GWzqXiNy8yHEqnfRnkIPIia2bj+h0gYODpzWLnh/ozw+VmQZukALRYKo6D+M9nBsm8MzitrEj35ApMuuZSeOmZiIQldCBtv7lBeq59PDVLOUowk/ZdlkwONQnIOrcgo0BPR/XxqoLg3h0E9QnSGtr4qJFhUB2/cfAgMBAAECggEBAKiaXq9vWXicrCM6sMz7H4oJAPsoKOhualm8TVz2d1CAPzqBqFN3lu6RFqzq3VDZ2tZ71GgARdegBf5AfJrNBWxupaENFv3J65gfDNCmVXbPlvR2cvcx9LGS4Ao60OunjBevfeMi0gVBzFTMpt0N1lyHNg6xEDjw3LNFgWE/ZKYx8pR2CvbqiJD2QBhMGM5z8bYc4B11D4rQ4OKQQdp0pIb8N74vuK4mDYjJGdcgywp/KBkixAIBgTZhwh0ZJYB0VkPFrMzqUBAA3RQeyVpYylaBkDkMxOQwz2lIK38jh8kCpQjKe4sNM4yGXnxH3Mx5r41ApFrR3QviDJEw9GH0E2ECgYEA9Z/CNIYmcR6Pv1K5xVyTymFqH1RAbqJUEaDAE1eIO+azBFW3i2Qe42b2neANBBZFbFq2nQej5TG4L8WCR36NDTdPAOahSvfUHvYge/wJqjH/emG6NGH1JqtDveWi4JG0wq7HzX34XQ6IGYQQgLzWNzCa52QwxopW3PmHJitGtC8CgYEA3GYn1vv65gqiyVMDN2A0h+8aKO0fSX4O3dGwIXMXcfWd95CPqcId8JTUW8TCFGOsV2y6A6i9zogNWWsqie18Z5SU5Xuy7NQQ7r27M0/lBK29Fp4P/0X86n3WAkgnUs+dpPzmfUU1XkKdNb3nNREZZI4Pfh6mMnS97alIHJP2ABECgYEA8f/3XbyWf3k286/T7Inr6sfTwQ5HdTlmTv/sfqINTcdctXamoiWA8Vfuv1mSOWaf+8PcjBaM+jHkYNzeIQakflLx+ddBLxXB3sds9fs6on8kUTWcOiQRX4n58k0gDtZAHFgL74CfhT/eBBXEFLL48+Nlm3lxPQvrzUfkSkz1Gm0CgYBDpWsm9hItFyidqEf9cB7PdhjOZPFMak9/z8NCkQOqK5v/hzRBA16TH+sJ+5siDPGxp6J0Sw9u251VS6ozyVt8o0MMKGpjfDdyP3O8rRH63KSD06HPVRjqlBLigmjnZufY4vKPknVt5/4+p3nWdNgZX+zrZdbW97PvxRxoARXpMQKBgQD1F+fN7O8Gn4vVYkSu3Dew/2VQiF2P8MCThxvUk+7w0mrvGHfq1dgLZwsbc+g6epKvDnzuBx0DgHg5Ge0cCr5iT2Q1QRLSaaB3A+ixq3v5f1R9nae32+QeWbb8JZfvnFfUWqHZc9tP2g0o9z32VBsNL6LY7u9INV4j7EgNkrzdJA==";
//	static String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2i92J2AZ+mzOjviNcbjSUJ54nZHAaQXulXje5CJVR3id0V8XeOlNLYswS9+NI28Ff9+oaAi/LtsLr0pG46a6+v13YrdyKcpYU4F+btyikkiSLxsh+MCC3Sgf9vhDON2aLHNeIHtPWrJ6p2/2QFMLGGS9+oEWtISi50IyrzbOYFbfl/e0NqxjnzjKhSXc6u8D0FygWXJcOxFErz9uQvb3mcdlzaj0QFq9Haa+ApSfjubtpPBKUa9jlGohw1ZEoS5cglrOCAHTORKwp7Wk5v8IQuuBq6PnSsxcA9KZ/g1YC5VJz+MxGw/D0ap9uMAeDKnO1CerciCBv5qc+QB68xSZQIDAQAB";

	//不倒翁红包营销
	/*static String APP_ID="2018123162719605";
	static String APP_PRIVATE_KEY="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCA0ZCoR4ToxQ9tZMkXyK+kDUxj7DlCqjNMXlU3b2MuK/eC3I+GQa8dkI6LutjkMon+/sd+XwZOq+yx4Z2m+6tZjLdtw2JORo9aswigtz/awUZA/XezAA6BUMjUhZ9pp3jWw+6xpXVEb7cIf2LsRgMBSbFEl/cnNVdnhGKMSrmTrmtulpYat6g2vtAUXzzzcTolhN/vOBU2c6oC927SdGvs9/TR7B+poFZ6pZWSLRs6y5v54eOqhuSBVAhgn49v2Z6dB8s/AoxVcyHw/Ya4MCInsnQZaZgfJ9IbPNZyhlSiSSsl5DMUzOy74CBKSyNNBHkngpxU48AQiyMDgXHVoQQ9AgMBAAECggEAYhIp1VONpkDQe/Is/oR046qUK8ad2VkGcO+oZOnSUkZ6yw/r3MSI8zMossxvb1vin1WMv3LMtHxDWTFw+r3DOAvfYhtL7lfdr6l5uhGI9ZwLRlMyG/7BDEZafyGzuZYcczT7thdHbfC3IisJPuQWSZa29IfVS+5aOp8Y/8GsAvfB5via8pDhXlH1eLs02KbZ2Kgwq67keXFqt6ZkO7eBximLyD820W90F/U/cqDKvNLaos6/wjknX4a1Uc5AfRfiHf+UhvuNh1sI+FHiHxx8l6kMRr+AEDI02A7UM2cMQX1qoX2tTpOy5NXTsLUmDLcynqHPwOha/3laGkTSjwffgQKBgQDRBwHuCt6SCx9Nb2UZkmgcnwuhhZvowt/MBiRB1pUDLei6WOhZS7rw3NTAorItniRJYFfujhrp6A8roz3TWTQcS7gXjPHtF5pGd6kjYCen7LgLeKGIDKvRYw2N0n0iPHBJ3b7gR2UAq+YqnqI3Qgnf28Jb9snMvT/XJARXzMebjQKBgQCdxEhkPGfJcoZmoFXW436uhCI84lojcJ9uapQ1zvVUtPHj7Rcx5XkWJwdwCELXNmW2Nly4gqescRLCkdVFMvoxb2E8kGALnyAqIjP1ajQcqbbwIBRn9JcrrH+MwHJHa+H96E9HmGDYHv70VXJQu18/4DdBSZZqcddxfppKyfaHcQKBgESdYUPLq54TKpZXr79CMQzcJIxD+JbiiWZGydYW41lk05WAI91UH6x7QRdvEMAqvkyb5KF5YlpQA9LWRjA/gvya2nZYnP2wFsN0+j+Ti9DywsN5OMUZK7mTI1rfspXvcwi8UrD6nqwmwWpSInuzINysHdPkYf/kS7WKrCZKIWftAoGAQSzPQyl5Ls1zHH5sN8Pvl6TaiBBycGWcV38MD1aaHw5JR4GwrVpBNZeWCQBArKu6RRYcI9VF84Ua3OpmEppGpZl/sAdsxiBMp1tN37qqIJU0D+sQwwlWUiKG5tyx2TOGPuBzr/4RM/dKGCIcgaGGbdrwuLXvv7Wb7TUCBJd+kuECgYBAdr7l2HiWnEMf02quLNRo81FEk6Ky5f+oaDK5g1U17rTfJD8j0J3TTDMyW6wRgQPGt+iRxnHjbPcbTV0zSC7G1V3gLC456T/GhuDve0pRMDq/GRqzkSezhiLo13OIfQ63mDYLutXrbRkQx9NORxGmQ6n7R9a1PMN2sSaDEAleYw==";
	static String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgNGQqEeE6MUPbWTJF8ivpA1MY+w5QqozTF5VN29jLiv3gtyPhkGvHZCOi7rY5DKJ/v7Hfl8GTqvsseGdpvurWYy3bcNiTkaPWrMIoLc/2sFGQP13swAOgVDI1IWfaad41sPusaV1RG+3CH9i7EYDAUmxRJf3JzVXZ4RijEq5k65rbpaWGreoNr7QFF8883E6JYTf7zgVNnOqAvdu0nRr7Pf00ewfqaBWeqWVki0bOsub+eHjqobkgVQIYJ+Pb9menQfLPwKMVXMh8P2GuDAiJ7J0GWmYHyfSGzzWcoZUokkrJeQzFMzsu+AgSksjTQR5J4KcVOPAEIsjA4Fx1aEEPQIDAQAB";
*/
	//李豪口碑
	static String APP_ID="2019022463364107";
	static String APP_PRIVATE_KEY="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCq8uyOMszizxBNMai1OtOil+IWhbBl2yd2CqfL3YUqUANI/yfuQ6fllBmjbVQrz4j3pHe1qyw0B3PJ97La7PGr0yuqQgu/NnBzvGvqO4CNApnXBZjV1Xu/aNBWh9XFENf4ORU6nGNhj9jr4pJrN6l+5uJ8T5kYWqfHcUUYDMsbtu1YjQpSq/TIXetNKcZnuYsq5Qjera01odrJbhhDAoBs3RGhCWaBxv3MSFCz9xNumCmsoIQu29JhVz4/B55OdSebrTfATEzOwGxuFs4T0KXJj3dL10bQqHJqqDMnjaoJStart1F6mrgLkEKVs2aP09cVdbC1y/miCSI1y0diTgTdAgMBAAECggEATvEPPDtJUhO8u2kLN2WLBbKNvUjPRLoHZwNUqVgKfpekbLknf2fOyL2zeTyree/EmFdi0InTR9OJLOMtvNteXrKNn3oQYqSJGWkRjIEdxABHenwjL9v94U5NpyfjF7XHheEWZJKDcjIzQfrHEqwJoYiNXkqDsDNs9zTfa1O9F3aE8QJmYH5JFUT+k38mqR5DTMVgkLHwPzU8pdoEi9HGSuTZ2odFKRZ5/k/9Vu04hc5/fnUFIUomEiu3V8R8FzJjGrv2aCtGT5q5p1vGClYXsJEeeSEqX42o4CQEmBGPTrv0mAA5IeqU23hs9ktnx+8WAa+a/Ldpa1utdt2kfwaVIQKBgQD/BVWh9RPiAWUHUs60+Z0D/8bIdHZW1rKI1e5DKLLxc7FfBnuP2IjdkIDzXx96IqoM40lGaX1edroTgQB/DOuiuPKv3J0JJ2VNu82WNe+ObUmWUnhJTCBm/hLZKTsXi9KHiUPM4HtxTL3Dkud++KVUUVH6F8COGQP+nuiE0IHrXwKBgQCrmvQUfx6XHKkBi8zfazpEFKVHMsW1pGMBVCUh/tm5omPBxG7ZBs0WyWXRzXprl8w7Zky4z7qPBBxgPZ+NdhAY+XlalRqr18y6qSwS7ysJ57yDtEgnzlxB/FfdtvHxbiYPfX7x33Qld7kMUbKt+WHSreVZQyRWvJwskXIgdSV1QwKBgQCUEeDanJXiz7R9QBNM+PG19LjSguyDFz2qPayNyf+8OdRuvDDaIHu3ScPVixGXtLDPsthEzdNBGeaIlIpZOoNGg+RFP+7d9cXYEIcaBE8Hf2UOpuu4gz79DeCbvljVHxYqJAT93AlQi6JS/+Tx0CUOg/j5IPloiBXNrS6MjxQgOQKBgAdbm3+Ne8hK6EwyrFQgCt2EbRnCaYvCQqR58SWmAbvd5J0YSRBxJDYH6J+4Sbl3RsB9QGjkL0GWkYjm24J7P3FysOtbXUtk81hFjKg7LQM9tm2HO1jJllcV9MaC45jQej1LyjegtyAsI/kNP7YJ7VHVNvI+2L4HVELs8ZHGtBZ1AoGAJSXQ4l48ore33DmQ2HbZO6Yl+zgR4SW+2yjhaUTQso9JyCNEUMVokJOIfR9Ufos8BT9iOWZZS3UJLgQCgxWr47XoPCwOdRU2qcEqnmrqRAg72EAkvP35GiYhIcUFiI3gMVGme1Z35asA0zeP/1N467w4huxTO6KMDJNHhmf3vUk=";
	static String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggjqEShUHHUTin+0RrScfd6gWCCExRwZk8tg5mamQawRdVS5s5Da+/jOxnILTR90JrBr2Jw6o3V4CFZOK6dVK/sxoXRyoBL1dAUNc0lrwDwfe2oW/ETGOG4EujUgyHIJ6Hkv18p/AJIVXQI1Ju1J9phg46ZxB14jNgHJ5V7ezxP08DUlq8vOEvcqgfiDLmHodzKAwewAPwo5e/PSaz15oP8XWllg5rOwiGNUUyMXIx2/deeOF00IVAPF6xjSrcz1rgBqS99WDL+Jrn96SsoCTTLibUs1TxtKSx+wJfh1mRN2CDcbJTQboBP8BBVEAtDGapkMNWcy6i6D+9axiAcjBwIDAQAB";

	//lihai账户
	//李海 63-9560388152 2088132791313590  lihai_1107@163.com 2088902893165535
	//赵阳 zhaoyang0001@outlook.com 2088232780410063
//	private static String APP_ID = "2018061460387371";
//	private static String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDZ/MWM3V66xjYv8ptZhBPDKKeUfHAByxL0GuGxAs72Smuf8PFbkHourYlP3hF19hWhBBofqojPDvdxD5UBP61PvsXCpqRX/8vjgOWaG7Iz/PxG5w0O8QmT70SGRUNvz1eSPshviB1fXnKkm1CQJgymFw/ftvqPgdpHo6mKjNISyddolB57QJkMSNuixFQidiSgHh0fD/6GoJ+X7RDOJi97RktwBZv2m7r+NOmLyfsVntvUgUk4kDDVBNWEuhQ+X4IAwr4u64diCNcTKNIM88ZuSyuoeTbmm9jnsQ1cwe27u2lDpJoeq8hkAUVIvbj4uPAQdeHS6wb4sJa5cG74EQFzAgMBAAECggEAdpMh3oMYIV7qYOCGEoZevZzis0mRH9iYAcKRm9jcPWqz6neEwnrvi26IL7KrKtBmaYSytSDtdDw+6vg/5EMAAk3SgxRkdx3EiYc23cJNCCyICgVqvALvY9IWIzeP/ET77KhMHSccWyEkGVgG1bJs1PfcgaOl3eQTmT50XdJF/0Nc8hbPxaHvXUbZmzwmpzzGyvyGM/UQc5A190yYQmlbxScWIcW5LRCkv4muN8W0TCTRXTIcBfLSTZu25r3CxF3NfW8fhL+qM8phnMp0D1OE7yFKSHKFu1bEJ56S1AkosT37dtVH1VnYKtpd7FWb2OVtCE05XPQpgqfQ1BWpWhsGYQKBgQDsdOP+b5EnnBN5N3DqRdbgea2YAcn56giESOn3svdK+fXlvKFVgsvoUX7Yg7tr+wDpbsy3LaIEkCliNfyUt5P95O+dy2E+cFLeX/uGqLlWQSkJWGjjcuJqlNuy7eoAhmBpDbShdP7wAyNQhWJJxmlhVN7wuLdur9DiPx+FFkOA4wKBgQDsARjOMvuIzAg1dSToyukPK2RpRt8yGYFPWpHXK0WKYrAE5JrhgGp9zLM1z1kbFGAxVSRsEYfrRtT4KY8MBj3VXIeJzwal5o3pihCDfCFNaGXG/CzczJ2t9b8U1EFj/RJ3Y9dKzZyiOklCE8gvHZ63M0wS42YJ+Gvc3Xvh4HMyMQKBgD2WnKPzD03P20qhZCnBExzY1JxZKvCLQrih/T7lQIAo5yF1plgNf2r9fxqKBVE0yaIDmBLGMgMaQY0xHp7lyghBjx/8j1GiFBOT0IHchJmw0y1596f2jn7QUwEh8uc8GPSD+15qiNSfHJ2mgFlS8rPFVWRB2JVd+fxUELOrft2dAoGAP9NcHgfztt9XKP3xaXPW699UXJRqMGZtbkSURJjTScW+zNP2fx33ruX5YYgeFRDBoxXfr8pd8+dIGYVDxoC5oEZR8ZcnuR5NKufH54deiky9mb8BcwVzb2SStNNii+QZZmh+BSDuR4Fz5obrELL2BZ296S3OnsusgCL2KfRFs0ECgYBAQbK0ssFXH59SLrL4SjBUEbg7QviRgxv06axd4ScRASo6hDWRqBAPZ0JIFTxUtfuWl+ny/lkm5wthn0GpdAtWBaGImckxFcnapDJXGmsRGO/6zBqtZedl3fHUxKXmVVM8ob3Zgh0X9EVk5+WrJqBylyohgpbUMvqcj1aa9sCkfg==";
//	private static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoqlFtXy3yZltwcGHa03B/I+AbOnl4wc96UcTfvQqTqO5dvCEGjnKHbF38qaIx9b8AQc7DxXp1Sw7J3gM8tvF41BeSSJD00l3H+WlDHUS/NVyO+CcYHcmjE83hdVxJ47D8aDCakzcT5L+HxMErYXxnAMt707ZBYPZ3dZ+OQivrFC7AAIjUmSK9CYp4H8gg0FSCP78uWXlw1Z8ZinnrBtizR7KmoBSZglJgs6eM+DLtzMWzIYkK1YMphiI4lwEbiOAdJRp7R3hJnYbcQ7owCBgtbseYYHKnbWNRa4E+/txknXuIfKKm70HTFNYtyMZ+1vyVeglz5IOx0f4iaItFNSsjQIDAQAB";
	
	//liyingying账户  2088131603674881  
	//RSA(SHA256)密钥(推荐)
//	private static String APP_ID = "2018061760406471";
//	private static String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCMill66vJwpAQi1m2FUwbVCKQJygMW7y/shDaMcoeH3RVw89k4PWRZOUDU665zd3AhPGwSwjjA2kSXnV39jYy54flRQ7cyErtMfu9Z2DeuXR2d+iTFuqr14mrHqf4CpF8Pscw1Sje8vcyQ7uDMgq+Kx44OTavFCoFAc5779qePlR9FVyLtF3SxBZGaaitTO73C95v/SivrGky3cfQz+i5z0B6TjCPubw5K67AMlQp1NiU16XRjLCEXPsI1/cI5d9gH//bPSSC4eufCTOPqnafv8WBu4y7huXf2x6ETCC5cNeeU/J81LKnL8Ipdic58rZKGCnw4D7Xpfh1TDuQfl8IHAgMBAAECggEAMxZLhpoHPRjyA/PsZe2Tazcz4fcT/m89/XzmE1HCoImmNKP/jykJ5EEipwNxK7n8AajkJEGDvd7Z1j83HmcpA5z4MykpW8NhYGI4AXHOc8yXaoi2e0XqxWzjoebSVgWIJL1d3PU4o5xe00RyZQUQaKM3qU1kpkC2UUZ2QWMc790bCA80qXmY3ZpqqabXlP2CvvM26G7vw3BymR7Jc15pzCLOjB2d8Q7HWFZ0tCQD0oFh8womSIhXUEP/bjsu/S8NCHmI3acvOlRLOqve7kd16iGCKriH6Qry8ugireBY5AYbyeLjUEGZYw/Ay9d8+LcMLGUL7WTkHS7y3gOFMG3MMQKBgQDZl82NeIyoXPK8pOsfJUWpodNcX1wDds4W+yPoT2YrgO8GGyGXuupl4aW0bvhmZ+Dx97G5xiEaom36OJ7Pxmo8Smb6efNdMHO9Q2e8F4fZqZ+5suDGKNFO4EbZqtmq+Lnsjp8Wuj3vsz6pIEFmRDk3Z2hP2L9wA6kpOUc6HJCJGQKBgQClWNXngPYwb2s8VX6wwUwqwWKZDlil4Gj8sKx0Nmuop9hEDoCvAsGX9/32mgaYkeGqeab9HNOp9a1gbcm/lcXVhAlPIO030XMBixn3HJgDECIsMj0/NyQD7hj00a9Q+bvVtxBgEvH53cuCqH3m/OlDEMeWT8QkDB5u/c3cwS1oHwKBgCUFDLW7AsNKPQoXTOFZqAZ6P8mBd9gnCF0kkTFfANT59LCnQQndhS+cOmL+Vkil98FzEMu4h71OUUO3Cl3zNfPX/Lk0slDyg15571Yy8qv3jfM8Gb3dB+9RfPxfcvUBqDA0+6AUQ6Fab5i6oiXSCaIBd9/N0pQuK0OWOSYL4hNRAoGBAJqmUUM7UoiQ3brLuDg4QzZGNCM10OIs1+VolZQIhn9zpYpaCjAmos2xzN6Gx56R64DYUcCx4qM3vxbO+QDxY91FprNU96ragvexk/KV0vQidePihXiH6W6nlr39FT3mprHIjE9fyOVMdplZYJDYgpAQtKy5JJoWn6xuRMqx//c/AoGAN84SdwmDLADCbzYIAMA/Qm/zQ67c+Mppgkzb28KUxfZhmb43M3GonjIcU6eSrBMXYVei4KJCUUVHeiirEHhOU7WIUARCC+YHRuKaNoEbSXG/Lcq8KZvJUaOyQjYYB+7o77luon5S9P5982YxPubkF8iRzty/jzTMpCAObcWyrl0=";
//	private static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoZMHWh/67lD8Urv2ahKMWX/CotxsAJrVL3g+nofxyrryV0Aldu7rmn9vi2gq08sWIK8jJtd7WwjpE+VtSHNhfym7/J6HGCbZOVO9SMsM/9l54p3dD0ihM9BR+nunrKSsMVQqv8vAHG5L0BAVoIX4/3qogrnhkqtTp3dNL4caYuff+1gGorPegAvUoUTJXZva8SigPmoFx41S/JaY9BWkwUAKn+iiEAFQTpUYMgPKVOha/hcDUvWjWJAmR/3nDEtwL8D4VcJQTLCKmim5y+zjhxgBFNAb4EtZOgsBobaHdqPI+WWgRTVxtwDTQFs0knvElzsg+8WIFYMW8VrPvmU0iQIDAQAB";

	public static void main(String[] args) {
		try {
			AlipayPayTest test = new AlipayPayTest ();
			//test.pay();
			//test.dangmianfu();
			test.order();
			//test.transfer();
			//send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @描述:发红包
	 * @作者:nada
	 * @时间:2019/1/2
	 **/
	private static void send(){
		try {
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
			AlipayMarketingCampaignCashCreateRequest request = new AlipayMarketingCampaignCashCreateRequest();
			request.setBizContent("{" +
				"    \"coupon_name\":\"煜雨创建营销活动送红包\"," +
				"    \"prize_type\":\"random\"," +
				"    \"total_money\":\"1.20\"," +
				"    \"total_num\":\"2\"," +
				"    \"prize_msg\":\"煜雨\"," +
				"    \"start_time\":\"NowTime\"," +
				"    \"end_time\":\"2019-01-12 22:48:30\"," +
				"    \"merchant_link\":\"http://www.baidu.com\"," +
				"    \"send_freqency\":\"D3|L10\"" +
				"  }");
			request.setNotifyUrl("http://106.14.187.178/opendevtools/notify/do/fd2fe5b6-394b-4001-a5a9-7dd0ef1eb2d1");
			AlipayMarketingCampaignCashCreateResponse response = alipayClient.execute(request);
			if(response.isSuccess()){
				System.out.println("调用成功");
			} else {
				System.out.println("调用失败");
			}

			/*AlipayFundCouponOrderPagePayRequest request = new AlipayFundCouponOrderPagePayRequest();
			request.setBizContent("{" +
				"\"out_order_no\":\"80777352559380234535\"," +
				"\"out_request_no\":\"80777352556340xx4353578\"," +
				"\"order_title\":\"发送红包\"," +
				"\"amount\":100.00," +
				"\"pay_timeout\":\"1h\"," +
				"\"extra_param\":\"{\\\"merchantExt\\\":\\\"key=value\\\"}\"" +
				"}");
			AlipayFundCouponOrderPagePayResponse response = alipayClient.execute(request);
			if(response.isSuccess()){
				System.out.println("调用成功");
			} else {
				System.out.println("调用失败");
			}*/
		} catch (AlipayApiException e) {
			e.printStackTrace ();
		}
	}

	private void redpay() throws  Exception{
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
		AlipayFundCouponOrderDisburseRequest request = new AlipayFundCouponOrderDisburseRequest();
		request.setBizContent("{" +
			"\"out_order_no\":\"8077735255938023\"," +
			"\"deduct_auth_no\":\"2014031600002001260000001024\"," +
			"\"deduct_out_order_no\":\"8077735255937028\"," +
			"\"out_request_no\":\"8077735255634078\"," +
			"\"order_title\":\"红包打款\"," +
			"\"amount\":100.00," +
			"\"payee_user_id\":\"2088102138117431\"," +
			"\"payee_logon_id\":\"alitest@alipay.com\"," +
			"\"pay_timeout\":\"1h\"," +
			"\"extra_param\":\"{\\\"merchantExt\\\":\\\"key=value\\\"}\"" +
			"  }");
		AlipayFundCouponOrderDisburseResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}
	}

	/**
	 * @描述:分润结算
	 * @时间:2018年10月16日 下午2:31:29
	 */
	public void settle()throws AlipayApiException{
		/*AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APP_ID, APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
		AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
		String orderNo = "h520181118143039468970";
		List<Object> lists = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("trans_out", "2088131603674881");//分账支出方账户
		map.put("trans_in", "2088902893165535");//分账收入方账户
		map.put("amount",  new BigDecimal("1.61").setScale(2,BigDecimal.ROUND_HALF_DOWN));//分账的金额
		map.put("amount_percentage", 100);//分账信息中分账百分比。取值范围为大于0，少于或等于100的整数
		map.put("desc", "分润结算"+orderNo);//分账描述
		lists.add(map);
		JSONObject map2 = new JSONObject();
		map2.put("out_request_no", orderNo);//自行生成结算流水号 
		map2.put("trade_no", "2018101722001480181006141071");//支付宝订单号
		map2.put("royalty_parameters",lists);//分账明细信息
		map2.put("operator_id", "999941000001");//	操作员id
		System.out.println("组装:"+map2.toString());
		request.setBizContent(map2.toString());*/
		String orderNo = "h520181118143039468970";
    	AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
		AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
		List<Object> lists = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("trans_out","2088131603674881");
		map.put("trans_in", "2088902893165535");
		map.put("amount","5");
		map.put("amount_percentage","100");
		map.put("desc", "分润结算"+orderNo);
		lists.add(map);
		JSONObject map2 = new JSONObject();
		map2.put("out_request_no",orderNo);
		map2.put("trade_no",orderNo);
		map2.put("royalty_parameters",lists);
		map2.put("operator_id","2342");
		request.setBizContent(map2.toString());
		
		/*request.setBizContent("{" +
		"\"out_request_no\":\"20160727001\"," +
		"\"trade_no\":\"2018101622001480180566210220\"," +
		"      \"royalty_parameters\":[{" +
		"        \"trans_out\":\"2088231855126313\"," +
		"\"trans_in\":\"2088231650723601\"," +
		"\"amount\":0.1," +
		"\"amount_percentage\":100," +
		"\"desc\":\"李海分润结算\"" +
		"        }]," +
		"\"operator_id\":\"A0001\"" +
		"  }");*/
		AlipayTradeOrderSettleResponse response = alipayClient.execute(request);
		System.out.println(response.getBody());
		if(response.isSuccess()){
		System.out.println("调用成功");
		} else {
		System.out.println("调用失败");
		}
	}

	private static void rongyaosend(){
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
		//发红包
		AlipayFundCouponOrderPagePayRequest request = new AlipayFundCouponOrderPagePayRequest();
		AlipayFundCouponOrderPagePayModel model = new AlipayFundCouponOrderPagePayModel();
		request.setBizModel(model);
		model.setAmount("1.02");
		model.setOutOrderNo(System.currentTimeMillis ()+"");
		model.setOutRequestNo(Constant.getDateString());
		try {
			AlipayFundCouponOrderPagePayResponse response = alipayClient.execute(request);
			System.out.println("=====================");
			System.out.println(response.getBody());
			System.out.println("=====================");
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @描述:下单测试
	 * @时间:2018年6月14日 下午5:33:09
	 */
	public void order()throws AlipayApiException{
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
		AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
		request.setBizModel(model);
		model.setOutTradeNo(System.currentTimeMillis()+"");
		model.setTotalAmount("1.88");
		model.setSubject("李海测试");
		AlipayTradePrecreateResponse response = alipayClient.execute(request);
		System.out.print(response.getBody());
		System.out.print(response.getQrCode());
	}
	
	/**
	 * @描述:当面付
	 * @时间:2018年6月18日 上午11:49:05
	 */
	public void dangmianfu()throws AlipayApiException{
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
		AlipayTradePayRequest request = new AlipayTradePayRequest(); //创建API对应的request类
		request.setBizContent("{" +
		"    \"out_trade_no\":\"20150320010101001\"," +
		"    \"scene\":\"bar_code\"," +
		"    \"auth_code\":\"28763443825664394\"," +
		"    \"subject\":\"Iphone6 16G\"," +
		"    \"store_id\":\"NJ_001\"," +
		"    \"timeout_express\":\"2m\"," +
		"    \"total_amount\":\"88.88\"" +
		"  }"); //设置业务参数
		AlipayTradePayResponse response = alipayClient.execute(request); //通过alipayClient调用API，获得对应的response类
		System.out.print(response.getBody());
	}

	/**
	 * @描述:转账支付
	 * @时间:2018年6月14日 下午5:33:19
	 */
	public void transfer()throws AlipayApiException{
		//SDK调用前需要进行初始化
		AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
		AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
		request.setBizContent("{" +
		"\"out_biz_no\":\"3142321423432\"," +
		"\"payee_type\":\"ALIPAY_LOGONID\"," +
		"\"payee_account\":\"lihai_1107@163.com\"," +
		"\"amount\":\"0.23\"," +
		"\"payer_show_name\":\"李海\"," +
		"\"payee_real_name\":\"李海\"," +
		"\"remark\":\"转账备注\"" +
		"}");
		AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
		System.out.print(response.getBody());
		JSONObject res = JSONObject.parseObject(response.getBody());
		if(response.isSuccess()){
			System.out.println("调用成功"+res.getString("sub_msg"));
		} else {
			System.out.println("调用失败"+res.getString("sub_msg"));
		}
	}

	public void test(){
		//AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "UTF-8", aliPublicKey, "RSA2");

		//口碑交易
//        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
//        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
//        request.setReturnUrl("http://");
//        request.setNotifyUrl("http://");
//        String orderNo = String.valueOf(System.currentTimeMillis());
//        System.out.println(orderNo);
//        request.setBizModel(model);
//        model.setOutTradeNo(orderNo);
//        model.setTotalAmount("0.2");
//        model.setSubject("测试");
//        try {
//            AlipayTradePrecreateResponse response = alipayClient.execute(request);
//            System.out.println(response.getQrCode());
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
		//手机网站支付
//        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
//        request.setNotifyUrl("http:/funpMerchH5CallBackApi");//服务器异步通知页面路径
//        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
//        request.setBizModel(model);
//        model.setSubject("test001");
//        model.setOutTradeNo(Constant.getAutoOrderNo());
//        model.setTotalAmount("0.02");
//        model.setTimeoutExpress("5m");
//        try {
//            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
//            System.out.println(response.getBody());
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }

		//订单查询
//        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
//        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
//        model.setOutTradeNo("sc181228131212663632");
//        model.setTradeNo("2018122822001404720551015370");
//        request.setBizModel(model);
//        try {
//            AlipayTradeQueryResponse response = alipayClient.execute(request);
//            System.out.println(response.getBody());
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }

		//查询对账单下载地址
//        try {
//            AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
//            AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
//            //账单类型：trade、signcustomer；trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的帐务账单
//            model.setBillType("signcustomer");
//            model.setBillDate("2018-12-28");
//            request.setBizModel(model);
//            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
//            System.out.println(response.getBody());
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
	}
}

