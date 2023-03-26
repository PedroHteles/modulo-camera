import React from 'react';
import { View, Button, NativeModules } from 'react-native';


interface MyCameraNativeProps {}
const CameraNative = (props : MyCameraNativeProps) => {
    const {QRCodeScanner} = NativeModules;

    const  onPress = async () => {
        try {
        let eventId = await QRCodeScanner.scanQRCode("qr")
        console.log(`Created a new event with id ${eventId}`);
        } catch (e) {
        console.error(e);
        }
    };


  return (
    <View>
      <Button
        title={'Abrir Camera'}
        onPress={onPress}
      />
    </View>
  );
};

export default CameraNative;
