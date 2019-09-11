package com.work.fyc.MyWork.mvp;

import com.work.fyc.MyWork.entity.CardEntity;
import com.work.fyc.MyWork.entity.SectionTabEntity;

import java.util.ArrayList;

public interface IMainView {
    void failToast(String message);

    void returnSectionCards(ArrayList<SectionTabEntity> sectionTabs, ArrayList<CardEntity> card, String section);

    void deleteSuccess(int position);

    void topSuccess(int position);
}
