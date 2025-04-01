import React, { useState } from 'react';
import { 
  Button, 
  SafeAreaView, 
  ScrollView, 
  StyleSheet, 
  Text, 
  TextInput, 
  View, 
  Alert, 
  Platform, 
  PermissionsAndroid, 
  TouchableOpacity
} from 'react-native';
import { NativeModules } from 'react-native';

const { ResumeGenerator } = NativeModules;

const ResumeForm = () => {
  // Personal & Contact Information
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [mobile, setMobile] = useState('');
  const [linkedin, setLinkedin] = useState('');
  const [github, setGithub] = useState('');

  // Objective & Experience
  const [Summary, setSummary] = useState('');
  const [experience, setExperience] = useState('');

  // Education Details
  // Undergraduate
  const [undergradDegree, setUndergradDegree] = useState('');
  const [undergradInstitution, setUndergradInstitution] = useState('');
  const [undergradCgpa, setUndergradCgpa] = useState('');
  // Higher Secondary
  const [higherInstitution, setHigherInstitution] = useState('');
  const [higherCgpa, setHigherCgpa] = useState('');
  // Secondary
  const [secondaryInstitution, setSecondaryInstitution] = useState('');
  const [secondaryCgpa, setSecondaryCgpa] = useState('');

  // Skills, Projects & Awards
  const [skills, setSkills] = useState('');
  const [projects, setProjects] = useState('');
  const [projectTitle, setProjectTitle] = useState('');
  const [awards, setAwards] = useState('');

  // Dynamic additional sections (if needed)
  const [sections, setSections] = useState<{ heading: string; content: string }[]>([]);

  const addSection = () => {
    setSections([...sections, { heading: '', content: '' }]);
  };

  const removeSection = (index: number) => {
    const newSections = sections.filter((_, i) => i !== index);
    setSections(newSections);
  };

  const updateSectionHeading = (text: string, index: number) => {
    const newSections = [...sections];
    newSections[index].heading = text;
    setSections(newSections);
  };

  const updateSectionContent = (text: string, index: number) => {
    const newSections = [...sections];
    newSections[index].content = text;
    setSections(newSections);
  };

  const requestStoragePermission = async () => {
    if (Platform.OS === 'android' && Platform.Version < 29) {
      try {
        const granted = await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
          {
            title: 'Storage Permission',
            message:
              'This app needs access to your storage to save the generated resume.',
            buttonNeutral: 'Ask Me Later',
            buttonNegative: 'Cancel',
            buttonPositive: 'OK',
          }
        );
        return granted === PermissionsAndroid.RESULTS.GRANTED;
      } catch (err) {
        console.warn(err);
        return false;
      }
    }
    return true;
  };

  const handleSubmit = async () => {
    const hasPermission = await requestStoragePermission();
    if (!hasPermission) {
      Alert.alert('Permission Denied', 'Cannot generate resume without storage permission.');
      return;
    }

    // Build the resume data object with all the fields required for the PDF
    const resumeData = {
      name,
      email,
      address,
      mobile,
      linkedin,
      github,
      Summary,
      experience,
      undergradDegree,
      undergradInstitution,
      undergradCgpa,
      higherInstitution,
      higherCgpa,
      secondaryInstitution,
      secondaryCgpa,
      skills,
      projects,
      projectTitle,
      awards,
      sections, 
    };

    await ResumeGenerator.generateResume(resumeData, 1)
      .then((result: string) => {
        Alert.alert('Resume Generated', `PDF saved at: ${result}`);
      })
      .catch((error: any) => {
        Alert.alert('Error', error.message || 'Failed to generate resume.');
      });
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.form}>
        {/* Personal Information */}
        <Text style={styles.label}>Name</Text>
        <TextInput onChangeText={setName} value={name} placeholder='Name' style={styles.input} />

        <Text style={styles.label}>Email</Text>
        <TextInput onChangeText={setEmail} value={email} placeholder='Email' style={styles.input} />

        <Text style={styles.label}>Address</Text>
        <TextInput onChangeText={setAddress} value={address} placeholder='Address' style={styles.input} />

        <Text style={styles.label}>Mobile</Text>
        <TextInput onChangeText={setMobile} value={mobile} placeholder='Mobile' style={styles.input} />

        <Text style={styles.label}>LinkedIn</Text>
        <TextInput onChangeText={setLinkedin} value={linkedin} placeholder='LinkedIn URL' style={styles.input} />

        <Text style={styles.label}>GitHub</Text>
        <TextInput onChangeText={setGithub} value={github} placeholder='GitHub URL' style={styles.input} />

        {/* Objective & Experience */}
        <Text style={styles.label}>Objective (Summary)</Text>
        <TextInput
          onChangeText={setSummary}
          value={Summary}
          placeholder='Objective / Summary'
          style={[styles.input, styles.multiLineInput]}
          multiline
        />

        <Text style={styles.label}>Experience</Text>
        <TextInput onChangeText={setExperience} value={experience} placeholder='Experience' style={styles.input} />

        {/* Education Section */}
        <Text style={styles.sectionHeader}>Education (Undergraduate)</Text>
        <Text style={styles.label}>Degree</Text>
        <TextInput onChangeText={setUndergradDegree} value={undergradDegree} placeholder='Degree' style={styles.input} />
        <Text style={styles.label}>Institution</Text>
        <TextInput onChangeText={setUndergradInstitution} value={undergradInstitution} placeholder='Institution' style={styles.input} />
        <Text style={styles.label}>CGPA</Text>
        <TextInput onChangeText={setUndergradCgpa} value={undergradCgpa} placeholder='CGPA' style={styles.input} />

        <Text style={styles.sectionHeader}>Education (Higher Secondary)</Text>
        <Text style={styles.label}>Institution</Text>
        <TextInput onChangeText={setHigherInstitution} value={higherInstitution} placeholder='Institution' style={styles.input} />
        <Text style={styles.label}>CGPA</Text>
        <TextInput onChangeText={setHigherCgpa} value={higherCgpa} placeholder='CGPA' style={styles.input} />

        <Text style={styles.sectionHeader}>Education (Secondary)</Text>
        <Text style={styles.label}>Institution</Text>
        <TextInput onChangeText={setSecondaryInstitution} value={secondaryInstitution} placeholder='Institution' style={styles.input} />
        <Text style={styles.label}>CGPA</Text>
        <TextInput onChangeText={setSecondaryCgpa} value={secondaryCgpa} placeholder='CGPA' style={styles.input} />

        {/* Skills, Projects & Awards */}
        <Text style={styles.label}>Skills</Text>
        <TextInput
          onChangeText={setSkills}
          value={skills}
          placeholder='Skills'
          style={[styles.input, styles.multiLineInput]}
          multiline
        />

        <Text style={styles.label}>Projects</Text>
        
        <TextInput
          onChangeText={setProjects}
          value={projects}
          placeholder='Projects'
          style={[styles.input, styles.multiLineInput]}
          multiline
        />

        <Text style={styles.label}>Awards & Certifications</Text>
        <TextInput
          onChangeText={setAwards}
          value={awards}
          placeholder='Awards & Certifications'
          style={[styles.input, styles.multiLineInput]}
          multiline
        />

        {/* Additional Dynamic Sections */}
        {sections.map((section, index) => (
          <View key={index} style={styles.sectionContainer}>
            <Text style={styles.label}>Section Heading</Text>
            <TouchableOpacity style={{ position: 'absolute', right: 10, top: 10 }} onPress={() => removeSection(index)}>
              <Text>-</Text>
            </TouchableOpacity>
            <TextInput
              value={section.heading}
              style={styles.input}
              onChangeText={(text) => updateSectionHeading(text, index)}
              placeholder="Heading"
            />
            <Text style={styles.label}>Section Content</Text>
            <TextInput
              value={section.content}
              style={[styles.input, styles.multiLineInput]}
              onChangeText={(text) => updateSectionContent(text, index)}
              placeholder="Content"
              multiline
            />
          </View>
        ))}
        
        <TouchableOpacity style={{ marginTop: 10, alignItems: 'flex-end' }} onPress={addSection}>
          <Text style={{ fontSize: 15 }}>+</Text>
        </TouchableOpacity>
        
        <View style={styles.buttonContainer}>
          <Button title="Submit" onPress={handleSubmit} />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default ResumeForm;


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f3f3f3',
  },
  form: {
    padding: 20,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    marginTop: 12,
  },
  input: {
    backgroundColor: '#fff',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  multiLineInput: {
    height: 100,
    textAlignVertical: 'top',
  },
  sectionHeader: {
    fontSize: 18,
    fontWeight: '700',
    marginTop: 20,
    color: '#333',
  },
  buttonContainer: {
    marginTop: 24,
    marginBottom: 24,
   },
  sectionContainer: {
    marginTop: 16,
    padding: 10,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 6,
    backgroundColor: '#fafafa',
  },
});
