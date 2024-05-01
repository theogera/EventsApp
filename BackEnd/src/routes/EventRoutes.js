const express = require('express');

const Event = require('../models/Event'); 
const router = express.Router();


/******USED BY THEN ANDROID FRONT END*****/

// endpoint to fetch information of an event by its ID
router.get('/byId/:eventId', async (req, res) => {
    try {
        const event = await Event.findById(req.params.eventId);
        if (!event) {
            return res.status(404).json({ message: 'Event not found' });
        }
        res.json(event);
    } catch (error) {
        console.error('Error:', error);
        res.status(500).json({ message: 'Server Error' });
    }
})

//get categories
router.get('/byCategory/:category', async (req, res) => {
    const category = req.params.category;
    try {
        const events = await Event.find({ category: category });
        res.json(events);
    } catch (error) {
        console.error(error);
        res.status(500).send('Error fetching events by category');
    }
});

// Endpoint to update an event's remaining tickets
router.put('/:eventId', async (req, res) => {
    const { remainingTickets } = req.body;
    try {
        const updatedEvent = await Event.findByIdAndUpdate(req.params.eventId, 
            { $set: { remainingTickets: remainingTickets } },
            { new: true, runValidators: true }); 
        if (!updatedEvent) {
            return res.status(404).send('Event not found');
        }
        res.json(updatedEvent);
    } catch (error) {
        console.error(error);
        res.status(500).send('Server error');
    }
});


/******MAY BE USED IN FUTURE UPDATES*****/

//api endpoint that shows all events
router.get('/', async (req, res) => {
    try {
        const events = await Event.find();
        res.json(events);
    } catch (error) {
        res.status(500).send("Error fetching events");
    }
});

// Endpoint to get events based on category and city 
router.get('/:cityId/:category', async (req, res) => {
    const { cityId, category } = req.params;

    try {
        const events = await Event.find({
            city: cityId,
            category: category
        });

        res.json(events);
    } catch (error) {
        console.error(error);
        res.status(500).send('Server error');
    }
});

//get categories
router.get('/:category', async (req, res) => {
    const category = req.params.category;
    try {
        const events = await Event.find({ category: category });
        res.json(events);
    } catch (error) {
        console.error(error);
        res.status(500).send('Error fetching events by category');
    }
});



module.exports = router;
