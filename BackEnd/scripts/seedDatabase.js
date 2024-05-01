require('dotenv').config();
const mongoose = require('mongoose');
const City = require('../src/models/City'); 
const Event = require('../src/models/Event');

const AthensEvents = require('./AthensEvents');
const ThessalonikiEvents = require('./ThessalonikiEvents');
const SerresEvents = require('./SerresEvents');
const CorfuEvents = require('./CorfuEvents');

const connectDB = async () => {
    try {
        await mongoose.connect('mongodb+srv://codeTeam1:Ip9GYXrIRgyRzA0X@cluster0.lpprxai.mongodb.net/');
        console.log(`MongoDB Connected`);
    } catch (error) {
        console.error(`Failed to connect to MongoDB: ${error}`);
        process.exit(1);
    }
};

const cities = [
    { title: 'Αθήνα', image: 'https://drive.google.com/uc?export=view&id=1Sw2EwIsDiFR9_Ar5M2rtqqR9xzoIlgzN', numOfEvents: 0 },
    { title: 'Θεσσαλονίκη', image: 'https://drive.google.com/uc?export=view&id=1Lz1OskBwJe_S0oiujfn6dElZHeFG5d7v', numOfEvents: 0 },
    { title: 'Κέρκυρα', image: 'https://drive.google.com/uc?export=view&id=1KwXtNsqHUHU1vRm2-AwxbTSPGDI4LBNx', numOfEvents: 0 },
    { title: 'Σέρρες', image: 'https://www.desknet.gr/wp-content/uploads/2022/07/img_2.jpg', numOfEvents: 0 }
  ];

  async function addEventsForCity(cityTitle, eventArray) {
    const city = await City.findOne({ title: cityTitle });
    if (!city) {
      console.log(`City not found: ${cityTitle}`);
      return;
    }

    let eventCount = 0; 

    for (const eventData of eventArray) {
      const newEvent = new Event({ ...eventData, city: city._id });
      await newEvent.save();
      city.events.push(newEvent._id);
      eventCount++; 
    }

    // Update the numOfEvents field for the city
    city.numOfEvents = eventCount;

    await city.save();
    console.log(`Events added and numOfEvents updated for ${cityTitle}`);
}


async function seedDB() {
    await connectDB(); 

    try {
        console.log('Starting the database seeding...');
        await City.deleteMany({});
        await Event.deleteMany({});
        console.log('Database cleared');

        // Create and save city documents
        for (let city of cities) {
            const newCity = new City(city);
            await newCity.save();
        }
        console.log('Cities added');

        //  add events to each city
        await addEventsForCity('Αθήνα', AthensEvents);
        await addEventsForCity('Θεσσαλονίκη', ThessalonikiEvents);
        await addEventsForCity('Σέρρες', SerresEvents);
        await addEventsForCity('Κέρκυρα', CorfuEvents);

        console.log('All events added successfully');
    } catch (error) {
        console.error('An error occurred:', error);
    } finally {
        mongoose.connection.close();
    }
}

seedDB();
