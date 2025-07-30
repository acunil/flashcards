import React, { useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

export default function App() {
  const [isFlipped, setIsFlipped] = useState(false);

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.card}
        onPress={() => setIsFlipped(!isFlipped)}
      >
        <Text style={styles.text}>
          {isFlipped ? 'Back of Card' : 'Front of Card'}
        </Text>
      </TouchableOpacity>
      <View style={styles.buttons}>
        <TouchableOpacity style={styles.button}>
          <Text>Easy</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button}>
          <Text>Medium</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.button}>
          <Text>Hard</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  card: {
    width: 300,
    height: 200,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 10,
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
  },
  text: {
    fontSize: 24,
  },
  buttons: {
    flexDirection: 'row',
    marginTop: 20,
  },
  button: {
    marginHorizontal: 10,
    padding: 10,
    backgroundColor: '#ddd',
    borderRadius: 5,
  },
});