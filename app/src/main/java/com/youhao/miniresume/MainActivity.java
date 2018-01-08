package com.youhao.miniresume;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.youhao.miniresume.model.BasicInfo;
import com.youhao.miniresume.model.Education;
import com.youhao.miniresume.model.Experience;
import com.youhao.miniresume.model.Project;
import com.youhao.miniresume.util.DateUtils;
import com.youhao.miniresume.util.ImageUtils;
import com.youhao.miniresume.util.ModelUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int REQ_CODE_EDIT_EDUCATION = 100;
    public static final int REQ_CODE_EDIT_EXPERIENCE = 200;
    public static final int REQ_CODE_EDIT_PROJECT = 300;
    private static final int REQ_CODE_EDIT_BASIC_INFO = 400;

    private BasicInfo basicInfo;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Project> projects;

    private static final String MODEL_EDUCATIONS = "educations";
    private static final String MODEL_EXPERIENCES = "experiences";
    private static final String MODEL_PROJECTS = "projects";
    private static final String MODEL_BASIC_INFO = "basic_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();
        setupUI();
    }

    private void setupUI() {
        setContentView(R.layout.activity_main);

        //add education
        findViewById(R.id.add_education_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditEducationActivity.class);
                startActivityForResult(intent, REQ_CODE_EDIT_EDUCATION);
            }
        });

        //add experience
        findViewById(R.id.add_experience_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditExperienceActivity.class);
                startActivityForResult(intent, REQ_CODE_EDIT_EXPERIENCE);
            }
        });

        //add project
        findViewById(R.id.add_project_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProjectActivity.class);
                startActivityForResult(intent, REQ_CODE_EDIT_PROJECT);
            }
        });

        setupBasicInfo();
        setupEducations();
        setupExperiences();
        setupProjects();

    }

    //get edit object information from editActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_EDIT_BASIC_INFO:
                    BasicInfo basicInfo = data.getParcelableExtra(BasicInfoEditActivity.KEY_BASIC_INFO);
                    updateBasicInfo(basicInfo);
                    break;

                case REQ_CODE_EDIT_EDUCATION:
                    String educationId = data.getStringExtra(EditEducationActivity.KEY_EDUCATION_ID);
                    if(educationId != null) {
                        deleteEducation(educationId);
                    } else {
                        Education education = data.getParcelableExtra(EditEducationActivity.KEY_EDUCATION);
                        updateEducations(education);
                    }
                    break;

                case REQ_CODE_EDIT_EXPERIENCE:
                    String experienceId = data.getStringExtra(EditExperienceActivity.KEY_EXPERIENCE_ID);
                    if(experienceId != null) {
                        deleteExperience(experienceId);
                    } else {
                        Experience experience = data.getParcelableExtra(EditExperienceActivity.KEY_EXPERIENCE);
                        updateExperiences(experience);
                    }
                    break;

                case REQ_CODE_EDIT_PROJECT:
                    String projectId = data.getStringExtra(EditProjectActivity.KEY_PROJECT_ID);
                    if(projectId != null) {
                        deleteProject(projectId);
                    } else {
                        Project project = data.getParcelableExtra(EditProjectActivity.KEY_PROJECT);
                        updateProjects(project);
                    }
                    break;
            }
        }
    }

    private void setupBasicInfo() {
        ((TextView) findViewById(R.id.name)).setText(TextUtils.isEmpty(basicInfo.name)
                ? "Your name"
                : basicInfo.name);
        ((TextView) findViewById(R.id.email)).setText(TextUtils.isEmpty(basicInfo.email)
                ? "Your email"
                : basicInfo.email);

        ImageView userPicture = (ImageView) findViewById(R.id.user_picture);
        if (basicInfo.imageUri != null) {
            ImageUtils.loadImage(this, basicInfo.imageUri, userPicture);
        } else {
            userPicture.setImageResource(R.drawable.user_ghost);
        }

        findViewById(R.id.edit_basic_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BasicInfoEditActivity.class);
                intent.putExtra(BasicInfoEditActivity.KEY_BASIC_INFO, basicInfo);
                startActivityForResult(intent, REQ_CODE_EDIT_BASIC_INFO);
            }
        });
    }

    private void setupEducations() {
        LinearLayout educationsLayout = (LinearLayout) findViewById(R.id.education_list);
        educationsLayout.removeAllViews();
        for(Education education: educations) {
            educationsLayout.addView(getEducationView(education));
        }
    }

    private void setupExperiences() {
        LinearLayout experiencesLayout = (LinearLayout) findViewById(R.id.experience_list);
        experiencesLayout.removeAllViews();
        for(Experience experience: experiences) {
            experiencesLayout.addView(getExperienceView(experience));
        }
    }

    private void setupProjects() {
        LinearLayout projectsLayout = (LinearLayout) findViewById(R.id.project_list);
        projectsLayout.removeAllViews();
        for(Project project: projects) {
            projectsLayout.addView(getProjectView(project));
        }
    }


    private View getEducationView(final Education education) {

        View educationView = getLayoutInflater().inflate(R.layout.education_item, null, false);
        String dateString = DateUtils.dateToString(education.startDate)
                + " ~ " + DateUtils.dateToString(education.endDate);
        ((TextView) educationView.findViewById(R.id.education_school))
                .setText(education.school + " " + education.major + " (" + dateString + ")");
        ((TextView) educationView.findViewById(R.id.education_courses))
                .setText(formatItems(education.courses));

        //call edit function from here

        ImageButton editEducationBtn = (ImageButton) educationView.findViewById(R.id.edit_education_btn);
        editEducationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditEducationActivity.class);
                intent.putExtra(EditEducationActivity.KEY_EDUCATION, education);
                startActivityForResult(intent, REQ_CODE_EDIT_EDUCATION);
            }
        });
        return educationView;
    }

    private View getExperienceView(final Experience experience) {

        View experienceView = getLayoutInflater().inflate(R.layout.experience_item, null, false);
        String dateString = DateUtils.dateToString(experience.startDate)
                + " ~ " + DateUtils.dateToString(experience.endDate);
        ((TextView) experienceView.findViewById(R.id.experience_company))
                .setText(experience.company + " " + experience.title + " (" + dateString + ")");
        ((TextView) experienceView.findViewById(R.id.experience_details))
                .setText(formatItems(experience.details));

        //call edit function from here

        ImageButton editExperienceBtn = (ImageButton) experienceView.findViewById(R.id.edit_experience_btn);
        editExperienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditExperienceActivity.class);
                intent.putExtra(EditExperienceActivity.KEY_EXPERIENCE, experience);
                startActivityForResult(intent, REQ_CODE_EDIT_EXPERIENCE);
            }
        });
        return experienceView;
    }

    private View getProjectView(final Project project) {

        View projectView = getLayoutInflater().inflate(R.layout.project_item, null, false);

        String dateString = DateUtils.dateToString(project.startDate)
                + " ~ " + DateUtils.dateToString(project.endDate);
        ((TextView) projectView.findViewById(R.id.project_name))
                .setText(project.name + " (" + dateString + ")");
        ((TextView) projectView.findViewById(R.id.project_details))
                .setText(formatItems(project.details));

        //call edit function from here

        ImageButton editProjectBtn = (ImageButton) projectView.findViewById(R.id.edit_project_btn);
        editProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProjectActivity.class);
                intent.putExtra(EditProjectActivity.KEY_PROJECT, project);
                startActivityForResult(intent, REQ_CODE_EDIT_PROJECT);
            }
        });
        return projectView;
    }

    private void deleteEducation(String educationId) {
        for(int i = 0; i < educations.size(); i++) {
            if(educations.get(i).id.equals(educationId)) {
                educations.remove(i);
                break;
            }
        }

        ModelUtils.save(this, MODEL_EDUCATIONS, educations);
        setupEducations();
    }

    private void deleteExperience(String experienceId) {
        for(int i = 0; i < experiences.size(); i++) {
            if(experiences.get(i).id.equals(experienceId)) {
                experiences.remove(i);
                break;
            }
        }

        ModelUtils.save(this, MODEL_EXPERIENCES, experiences);
        setupExperiences();
    }

    private void deleteProject(String projectId) {
        for(int i = 0; i < projects.size(); i++) {
            if(projects.get(i).id.equals(projectId)) {
                projects.remove(i);
                break;
            }
        }

        ModelUtils.save(this, MODEL_PROJECTS, projects);
        setupProjects();
    }

    private void updateBasicInfo(BasicInfo basicInfo) {
        ModelUtils.save(this, MODEL_BASIC_INFO, basicInfo);

        this.basicInfo = basicInfo;
        setupBasicInfo();
    }

    private void updateEducations(Education education) {
        boolean found = false;
        for(int i = 0; i < educations.size(); i++) {
            if(educations.get(i).id.equals(education.id)) {
                found = true;
                educations.set(i, education);
                break;
            }
        }

        if(!found) {
            educations.add(education);
        }

        ModelUtils.save(this, MODEL_EDUCATIONS, educations);
        setupEducations();
    }

    private void updateExperiences(Experience experience) {
        boolean found = false;
        for(int i = 0; i < experiences.size(); i++) {
            if(experiences.get(i).id.equals(experience.id)) {
                found = true;
                experiences.set(i, experience);
                break;
            }
        }

        if(!found) {
            experiences.add(experience);
        }

        ModelUtils.save(this, MODEL_EXPERIENCES, experiences);
        setupExperiences();
    }

    private void updateProjects(Project project) {
        boolean found = false;
        for(int i = 0; i < projects.size(); i++) {
            if(projects.get(i).id.equals(project.id)) {
                found = true;
                projects.set(i, project);
                break;
            }
        }

        if(!found) {
            projects.add(project);
        }

        ModelUtils.save(this, MODEL_PROJECTS, projects);
        setupProjects();
    }

    private void loadData() {
        BasicInfo savedBasicInfo = ModelUtils.read(this,
                MODEL_BASIC_INFO,
                new TypeToken<BasicInfo>(){});
        basicInfo = savedBasicInfo == null ? new BasicInfo() : savedBasicInfo;

        List<Education> savedEducation = ModelUtils.read(this,
                MODEL_EDUCATIONS,
                new TypeToken<List<Education>>(){});
        educations = savedEducation == null ? new ArrayList<Education>() : savedEducation;

        List<Experience> savedExperience = ModelUtils.read(this,
                MODEL_EXPERIENCES,
                new TypeToken<List<Experience>>(){});
        experiences = savedExperience == null ? new ArrayList<Experience>() : savedExperience;

        List<Project> savedProjects = ModelUtils.read(this,
                MODEL_PROJECTS,
                new TypeToken<List<Project>>(){});
        projects = savedProjects == null ? new ArrayList<Project>() : savedProjects;
    }

    public static String formatItems(List<String> items) {
        StringBuilder sb = new StringBuilder();
        for (String item: items) {
            sb.append(' ').append('-').append(' ').append(item).append('\n');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
