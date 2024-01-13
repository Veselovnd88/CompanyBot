package ru.veselov.companybot.cache.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.companybot.bot.BotState;
import ru.veselov.companybot.bot.keyboard.ContactKeyboardHelper;
import ru.veselov.companybot.cache.ContactCache;
import ru.veselov.companybot.cache.InquiryCache;
import ru.veselov.companybot.cache.UserStateCache;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.util.TestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDataCacheFacadeImplTest {

    @Mock
    ContactCache contactCache;

    @Mock
    InquiryCache inquiryCache;

    @Mock
    ContactKeyboardHelper contactKeyboardHelper;

    @Mock
    UserStateCache userStateCache;

    @InjectMocks
    UserDataCacheFacadeImpl userDataCacheFacade;

    @Test
    void shouldCallInternalCachesToClear() {
        userDataCacheFacade.clear(TestUtils.USER_ID);

        verify(contactCache).clear(TestUtils.USER_ID);
        verify(userStateCache).clear(TestUtils.USER_ID);
        verify(inquiryCache).clear(TestUtils.USER_ID);
        verify(contactKeyboardHelper).clear(TestUtils.USER_ID);
    }

    @Test
    void shouldCallUserStateCache() {
        userDataCacheFacade.getUserBotState(TestUtils.USER_ID);

        verify(userStateCache).getUserBotState(TestUtils.USER_ID);
    }

    @Test
    void shouldCallUserStateCacheToSetUp() {
        userDataCacheFacade.setUserBotState(TestUtils.USER_ID, BotState.BEGIN);

        verify(userStateCache).setUserBotState(TestUtils.USER_ID, BotState.BEGIN);
    }

    @Test
    void shouldCreateInquiryByInquiryCache() {
        DivisionModel division = TestUtils.getDivision();
        userDataCacheFacade.createInquiry(TestUtils.USER_ID, division);

        verify(inquiryCache).createInquiry(TestUtils.USER_ID, division);
    }

    @Test
    void shouldGetInquiryByInquiryCache() {
        userDataCacheFacade.getInquiry(TestUtils.USER_ID);

        verify(inquiryCache).getInquiry(TestUtils.USER_ID);
    }

    @Test
    void shouldCreateContactByContactCache() {
        userDataCacheFacade.createContact(TestUtils.USER_ID);

        verify(contactCache).createContact(TestUtils.USER_ID);
    }

    @Test
    void shouldGetContactByContactCache() {
        userDataCacheFacade.getContact(TestUtils.USER_ID);

        verify(contactCache).getContact(TestUtils.USER_ID);
    }
}