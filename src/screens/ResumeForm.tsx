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
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [Summary, setSummary] = useState('');
  const [mobile, setMobile] = useState('');
  const [experience, setExperience] = useState('');
  const [linkdin , setLinkdin] = useState('');
  const [github , setGithub] = useState('');

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
    // For Android 10+ and iOS, permission is not needed in the same way.
    return true;
  };

  const handleSubmit = async () => {
    const hasPermission = await requestStoragePermission();
    if (!hasPermission) {
      Alert.alert('Permission Denied', 'Cannot generate resume without storage permission.');
      return;
    }

    // Build the resume data object
    const resumeData = {
      name,
      email,
      Summary,
      mobile,
      linkdin,
      github,
      experience
    };

    // Call the native module with template 1 (default)
    ResumeGenerator.generateResume(resumeData, 2)
      .then((result: string) => {
        Alert.alert('Resume Generated', `PDF saved at: ${result}`);
      })
      .catch((error: any) => {
        Alert.alert('Error', error.message || 'Failed to generate resume.');
      });
  };

  const createInputElement =()=>{
    return (
      <TextInput
      style={styles.input}
      
      />
    )
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.form}>
        <Text style={styles.label}>Name</Text>
        <TextInput
          onChangeText={setName}
          value={name}
          placeholder='Name'
          style={styles.input}
        />
        <Text style={styles.label}>Email</Text>
        <TextInput
          value={email}
          onChangeText={setEmail}
          placeholder='Email'
          style={styles.input}
        />
        <Text style={styles.label}>Mobile</Text>
        <TextInput
          value={mobile}
          onChangeText={setMobile}
          placeholder='Mobile'
          style={styles.input}
        />
        <Text style={styles.label}>Linkdin</Text>
        <TextInput
          value={linkdin}
          onChangeText={setLinkdin}
          placeholder='Linkdin'
          style={styles.input}
        />
        <Text style={styles.label}>Github</Text>
        <TextInput
          value={github}
          onChangeText={setGithub}
          placeholder='github'
          style={styles.input}
        />
        <Text style={styles.label}>Summary</Text>
        <TextInput
          value={Summary}
          onChangeText={setSummary}
          style={[styles.input, styles.multiLineInput]}
          multiline
          placeholder='Summary'
        />
        <Text style={styles.label}>Experience</Text>
        <TextInput
          value={experience}
          onChangeText={setExperience}
          placeholder='Experience'
          style={styles.input}
        />
        <TouchableOpacity 
        style={{marginTop:10 , alignItems:'flex-end'}}
        onPress={createInputElement}>
          <Text style={{fontSize:15}}>+</Text>
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
  buttonContainer: {
    marginTop: 24,
  },
});
